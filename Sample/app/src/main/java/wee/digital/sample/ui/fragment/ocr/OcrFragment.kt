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
import wee.dev.weeocr.utils.BitmapUtils
import wee.digital.camera.resize
import wee.digital.camera.toBytes
import wee.digital.library.extension.decodeToBitmap
import wee.digital.library.extension.gone
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
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

    private var frameFullFont: Bitmap? = null

    private var frameFullBack: Bitmap? = null

    private var processing: Boolean = false

    private var frameComplete: Boolean = false

    private var isStart = false

    override fun layoutResource(): Int = R.layout.ocr

    override fun onViewCreated() {
        Configs.configWeeOcr()
        Shared.ocrCardFront.postValue(null)
        Shared.ocrCardBack.postValue(null)
        weeOcr = WeeOCR(requireActivity()).apply {
            this.initTemplateCCCD()
            this.initTemplateCMND()
        }
        addClickListener(ocrResetFont, ocrResetBack, ocrActionNext)
        resetAllFrame()
    }

    override fun onLiveDataObserve() {
        ocrVM.statusExtractFrontVP.observe {
            if (it.code != 0) {
                Shared.messageFail.postValue(
                        MessageData("Không thể đọc được dữ liệu", it.mess)
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.ocrCardInfoVP.postValue(it)
            ocrVM.scanOCRBackVP(
                    type = Configs.ID_CARD_BACK,
                    sessionId = Utils.getUUIDRandom(),
                    image = frameFullBack!!.resize(900, Bitmap.CompressFormat.JPEG).toBytes(),
            )
        }
        ocrVM.statusExtractBackVP.observe {
            if (it.code != 0) {
                Shared.messageFail.postValue(
                        MessageData("Không thể đọc được dữ liệu", it.mess)
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.ocrCardInfoVP.value?.issueDate = it.issueDate
            Shared.ocrCardInfoVP.value?.issueLoc = it.issueLoc
            navigateUI()
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
                ocrVM.scanOCRFrontVP(
                        type = Configs.ID_CARD_FRONT,
                        sessionId = Utils.getUUIDRandom(),
                        image = frameFullFont!!.resize(900, Bitmap.CompressFormat.JPEG).toBytes()
                )
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
        Log.e("frameCameraOcr", "start ${weeOcr == null}")
    }

    private fun cropFrame(frame: ByteArray) {
        if (processing) return
        processing = true
        weeOcr?.cropObjectRect(frame, true, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT) { cropped, type, typeFrontBack ->
            activity?.runOnUiThread {
                Log.e("typeScan2", "type : $type - typeScan : $typeCard")
                if (type == CAVET || type == NONE || cropped == null || !Utils.checkSizeBitmap(cropped)) {
                    processing = false
                    return@runOnUiThread
                }
                val frameFull = BitmapUtils.NV21toJPEG(frame, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT, 80).decodeToBitmap()
                if(frameFull == null){
                    processing = false
                    return@runOnUiThread
                }
                if (type != typeCard) resetAllFrame()
                typeCard = type
                bindFrame(cropped, typeFrontBack, frameFull)
            }
        }
    }

    private fun bindFrame(cropped: Bitmap, typeFrontBack: String, frameFull: Bitmap) {
        when (typeFrontBack) {
            SystemUrl.FRONT -> {
                if (frameFont != null) {
                    processing = false
                    return
                }
                frameFont = cropped
                frameFullFont = frameFull
                ocrResetFont.show()
                if (frameBack == null) Voice.ins?.request(VoiceData.CARD_1_OKE)
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
                frameFullBack = frameFull
                ocrResetBack.show()
                if (frameFont == null) Voice.ins?.request(VoiceData.CARD_1_OKE)
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
        Voice.ins?.request(VoiceData.CARD_2_OKE){}
        activity?.runOnUiThread { ocrActionNext.show() }
    }

    private fun resetAllFrame() {
        activity?.runOnUiThread {
            typeCard = ""
            frameBack = null
            frameFont = null
            frameFullFont = null
            frameFullBack = null
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
            frameFullFont = null
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
            frameFullBack = null
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
                    isStart = true
                    startCamera()
                    Voice.ins?.request(VoiceData.PUSH_ID_CARD) {}
                    Log.e("startCameraOcr", "Start camera")
                }, {})
    }

    override fun onPause() {
        super.onPause()
        disposableCamera?.dispose()
        release()
    }

}