package wee.digital.sample.ui.fragment.ocr

import android.graphics.Bitmap
import android.util.Base64
import android.view.View
import kotlinx.android.synthetic.main.ocr.*
import wee.dev.weeocr.WeeOCR
import wee.dev.weeocr.camera.CameraConfig
import wee.dev.weeocr.camera.CameraSource
import wee.dev.weeocr.camera.FrameStreamListener
import wee.dev.weeocr.repository.utils.SystemUrl
import wee.dev.weeocr.repository.utils.SystemUrl.CAVET
import wee.dev.weeocr.repository.utils.SystemUrl.NONE
import wee.digital.camera.toBytes
import wee.digital.library.extension.gone
import wee.digital.library.extension.post
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.sample.shared.Utils
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.animOcrCaptured
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment

class OcrFragment : MainFragment(), FrameStreamListener {

    private val ocrVM: OcrVM by lazy { viewModel(OcrVM::class) }

    private var camera: CameraSource? = null

    private var weeOcr: WeeOCR? = null

    private var typeCard = ""

    private var frameFont: Bitmap? = null

    private var frameBack: Bitmap? = null

    private var processing: Boolean = false

    private var frameComplete: Boolean = false

    override fun layoutResource(): Int = R.layout.ocr

    override fun onViewCreated() {
        createOcr()
        addClickListener(ocrResetFont, ocrResetBack, ocrActionNext)
    }

    override fun onLiveDataObserve() {
        ocrVM.statusExtractFront.observe {
            if(it.code != 0L){
                Shared.messageFail.postValue(
                        MessageData("Không thể đọc được dữ liệu", "không thể đọc giấy tờ, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.ocrCardFront.postValue(it)
            checkNavigate(dataFront = it)
        }
        ocrVM.statusExtractBack.observe {
            if(it.code != 0L){
                Shared.messageFail.postValue(
                        MessageData("Không thể đọc được dữ liệu",
                                "không thể đọc giấy tờ, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.ocrCardBack.postValue(it)
            checkNavigate(dataBack = it)
        }
    }

    private fun checkNavigate(dataFront: FrontCardResp? = Shared.ocrCardFront.value, dataBack: BackCardResp? = Shared.ocrCardBack.value) {
        if (dataFront != null && dataBack != null) {
            Socket.action.sendData(SocketReq(Configs.FORM_STEP_2, SocketData(photo = Shared.frameCardData.value)))
            navigate(MainDirections.actionGlobalOcrConfirmFragment())
        }else{
            toast("fail data ocr")
        }
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
                Shared.frameCardData.postValue(
                        PhotoCardInfo(
                                Base64.encodeToString(frameFont.toBytes(), Base64.NO_WRAP),
                                Base64.encodeToString(frameBack.toBytes(), Base64.NO_WRAP)
                        )
                )
                when (Shared.typeCardOcr.value) {
                    Configs.TYPE_NID -> {
                        ocrVM.extractNidFront(frameFont!!.toBytes())
                        ocrVM.extractNidBack(frameBack!!.toBytes())
                    }
                    Configs.TYPE_NID_12 -> {
                        ocrVM.extractNid12Front(frameFont!!.toBytes())
                        ocrVM.extractNid12Back(frameBack!!.toBytes())
                    }
                    Configs.TYPE_CCCD -> {
                        ocrVM.extractCccdFront(frameFont!!.toBytes())
                        ocrVM.extractCccdBack(frameBack!!.toBytes())
                    }
                }
            }
        }
    }

    private fun createOcr() {
        WeeOCR.CAMERA_SATURATION_STEP = "0"
        WeeOCR.THRESH_CROP = 64.0
        WeeOCR.BLUR_MIN_VALUE = 270
        WeeOCR.CAMERA_ZOOM = "18"
        Shared.ocrCardFront.postValue(null)
        Shared.ocrCardBack.postValue(null)
        weeOcr = WeeOCR(requireActivity())
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
        if (frameFont != null && frameBack != null) return
        cropFrame(byteArray)
    }

    private fun cropFrame(frame: ByteArray) {
        if (processing) return
        processing = true
        weeOcr?.cropObjectAlign(frame, true, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT) { cropped, type, typeFrontBack ->
            if (type == CAVET || type == NONE || cropped == null || !Utils.checkSizeBitmap(cropped)) {
                processing = false
                return@cropObjectAlign
            }
            if (type != typeCard) resetAllFrame()
            typeCard = type
            activity?.runOnUiThread { bindFrame(cropped, typeFrontBack) }
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
        ocrActionNext.show()
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
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        release()
    }

}