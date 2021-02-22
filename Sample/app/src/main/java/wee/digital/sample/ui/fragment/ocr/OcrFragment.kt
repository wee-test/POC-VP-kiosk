package wee.digital.sample.ui.fragment.ocr

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import android.view.View
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ocr.*
import wee.dev.weeocr.WeeOCR
import wee.dev.weeocr.camera.CameraConfig
import wee.dev.weeocr.camera.CameraSource
import wee.dev.weeocr.camera.FrameStreamListener
import wee.dev.weeocr.repository.utils.SystemUrl
import wee.dev.weeocr.repository.utils.SystemUrl.CAVET
import wee.dev.weeocr.repository.utils.SystemUrl.NONE
import wee.digital.camera.resize
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.gone
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.sample.shared.Utils
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.animOcrCaptured
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import java.util.concurrent.TimeUnit

class OcrFragment : MainFragment(), FrameStreamListener {

    private val ocrVM: OcrVM by lazy { viewModel(OcrVM::class) }

    private var disposableCamera: Disposable? = null

    private var camera: CameraSource? = null

    private var weeOcr: WeeOCR? = null

    private var typeCard = ""

    private var frameFont: Bitmap? = null

    private var frameBack: Bitmap? = null

    private var processing: Boolean = false

    private var frameComplete: Boolean = false

    private var isStart = false

    override fun layoutResource(): Int = R.layout.ocr

    override fun onViewCreated() {
        Configs.configWeeOcr()
        Shared.ocrCardFront.postValue(null)
        Shared.ocrCardBack.postValue(null)
        weeOcr = WeeOCR(requireActivity())
        addClickListener(ocrResetFont, ocrResetBack, ocrActionNext)
        resetAllFrame()
    }

    override fun onLiveDataObserve() {
        ocrVM.statusExtractFrontVP.observe {
            if (it.code != 0) {
                Shared.messageFail.postValue(
                        MessageData("Không thể đọc được dữ liệu",
                                "không thể đọc giấy tờ, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.ocrCardInfoVP.postValue(it)
            ocrVM.scanOCRBackVP(
                    type = Configs.ID_CARD_BACK,
                    sessionId = Utils.getUUIDRandom(),
                    image = frameBack!!.resize(640, Bitmap.CompressFormat.JPEG).toBytes(),
            )
        }
        ocrVM.statusExtractBackVP.observe {
            if (it.code != 0) {
                Shared.messageFail.postValue(
                        MessageData("Không thể đọc được dữ liệu",
                                "không thể đọc giấy tờ, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.ocrCardInfoVP.value?.issueDate = it.issueDate
            Shared.ocrCardInfoVP.value?.issueLoc = it.issueLoc
            navigateUI()
        }
        ocrVM.statusSearchVP.observe {
            if(it == null || it.responseCode.code != 0L){
                Shared.messageFail.postValue(
                        MessageData("Đăng ký thất bại",
                                "Không thể đăng ký tài khoản, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
            }
            if(it.result.data.isExisted == true){
                Shared.messageFail.postValue(
                        MessageData("Giấy tờ đã tồn tại",
                                "Không thể đăng ký tài khoản vì giấy tờ của bạn đã được đăng ký")
                )
                navigate(MainDirections.actionGlobalFailFragment())
            }else{
                ocrVM.scanOCRFrontVP(
                        type = Configs.ID_CARD_FRONT,
                        sessionId = Utils.getUUIDRandom(),
                        image = frameFont!!.resize(800, Bitmap.CompressFormat.JPEG).toBytes()
                )
            }
        }
    }

    private fun navigateUI() {
        sendSocket()
        navigate(MainDirections.actionGlobalOcrConfirmFragment())
    }

    private fun sendSocket() {
        val resp = Shared.socketReqData.value
        resp?.cmd = Configs.FORM_STEP_2
        resp?.data?.photo = Shared.frameCardData.value
        Socket.action.sendData(resp)
        Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
    }

    override fun onViewClick(v: View?) {
        when (v) {
            ocrResetFont -> {
                if (frameComplete) return
                resetFrameFont()
            }
            ocrResetBack -> {
                if (frameComplete) return
                resetFrameBack()
            }
            ocrActionNext -> {
                if (frameComplete) return
                frameComplete = true
                ocrActionNext.gone()
                ocrLoading.show()
                Shared.ocrCardInfoVP.postValue(CardRespVP())
                Shared.frameCardData.postValue(
                        PhotoCardInfo(
                                Base64.encodeToString(frameFont.toBytes(), Base64.NO_WRAP),
                                Base64.encodeToString(frameBack.toBytes(), Base64.NO_WRAP)
                        )
                )
                ocrVM.searchCustomer(frameFont.toBytes().toStringBase64())
            }
        }
    }

    private fun startCamera() {
        try {
            camera = CameraSource(requireActivity(), ocrGraphicOverlay).also {
                it.setFacing(CameraConfig.facing)
                ocrPreview.start(it, ocrGraphicOverlay)
                it.setFrameProcessorListener(this)
            }
        } catch (e: Exception) {
            toast("start camera error : ${e.message}")
        }
    }

    private fun release() {
        try {
            camera?.release()
            ocrPreview?.release()
            weeOcr?.destroy()
            camera = null
        } catch (e: Exception) {
            toast("stop camera error : ${e.message}")
        }
    }

    override fun onFrame(byteArray: ByteArray) {
        if(!isStart) return
        if (frameFont != null && frameBack != null) return
        cropFrame(byteArray)
    }

    private fun cropFrame(frame: ByteArray) {
        if (processing) return
        processing = true
        weeOcr?.cropObjectRect(frame, true, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT) { cropped, type, typeFrontBack ->
            activity?.runOnUiThread {
                if (type == CAVET || type == NONE || cropped == null || !Utils.checkSizeBitmap(cropped)) {
                    processing = false
                    return@runOnUiThread
                }
                if (type != typeCard) resetAllFrame()
                typeCard = type
                Log.e("typeScan", "type : $type - typeScan : $typeCard")
                bindFrame(cropped, typeFrontBack)
            }
        }
    }

    private fun bindFrame(cropped: Bitmap, typeFrontBack: String) {
        when (typeFrontBack) {
            SystemUrl.FRONT -> {
                if (frameFont != null) {
                    processing = false
                    return
                }
                frameFont = cropped
                ocrResetFont.show()
                if(frameBack==null)  Voice.ins?.request(VoiceData.CARD_1_OKE)
                ocrRoot.animOcrCaptured(cropped, orcFrameAnim, ocrFrameFront, ocrRootCamera) {
                    checkShowAction()
                    processing = false
                }
            }
            SystemUrl.BACK -> {
                if (frameBack != null) {
                    processing = false
                    return
                }
                frameBack = cropped
                ocrResetBack.show()
                if(frameFont==null)  Voice.ins?.request(VoiceData.CARD_1_OKE)
                ocrRoot.animOcrCaptured(cropped, orcFrameAnim, ocrFrameBack, ocrRootCamera) {
                    checkShowAction()
                    processing = false
                }
            }
            else -> processing = false
        }
    }

    private fun checkShowAction() {
        frameFont ?: return
        frameBack ?: return
        Voice.ins?.request(VoiceData.CARD_2_OKE){
            activity?.runOnUiThread {
                ocrActionNext.show()
            }
        }
    }

    private fun resetAllFrame() {
        activity?.runOnUiThread {
            typeCard = ""
            frameBack = null
            frameFont = null
            processing = false
            frameComplete = false
            ocrActionNext.gone()
            ocrFrameFront.setImageBitmap(null)
            ocrResetFont.gone()
            ocrFrameBack.setImageBitmap(null)
            ocrResetBack.gone()
            ocrLoading.gone()
        }
    }

    private fun resetFrameFont() {
        activity?.runOnUiThread {
            processing = false
            frameFont = null
            frameComplete = false
            ocrFrameFront.setImageBitmap(null)
            ocrResetFont.gone()
            ocrActionNext.gone()
            ocrLoading.gone()
        }
    }

    private fun resetFrameBack() {
        activity?.runOnUiThread {
            processing = false
            frameBack = null
            frameComplete = false
            ocrFrameBack.setImageBitmap(null)
            ocrResetBack.gone()
            ocrActionNext.gone()
            ocrLoading.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        disposableCamera = Single.timer(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    startCamera()
                    Voice.ins?.request(VoiceData.PUSH_ID_CARD) {
                        isStart = true
                    }
                }, {})
    }

    override fun onPause() {
        super.onPause()
        disposableCamera?.dispose()
        release()
    }

}