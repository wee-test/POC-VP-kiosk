package wee.digital.sample.ui.fragment.ocr

import android.graphics.Bitmap
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
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.library.util.Utils
import wee.digital.sample.MainDirections
import wee.digital.sample.R
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
        ocrVM.statusVerifyCard.observe {
            if (!it) {
                toast("fail verifyCard")
                return@observe
            }
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
        ocrVM.statusExtractFront.observe {
            navigate(MainDirections.actionGlobalOcrConfirmFragment())
        }
        ocrVM.statusExtractBack.observe {
            navigate(MainDirections.actionGlobalOcrConfirmFragment())
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
                ocrVM.verifyIdCard(frameFont!!.toBytes(), Shared.faceCapture.value!!.toBytes())
            }
        }
    }

    private fun createOcr() {
        WeeOCR.CAMERA_SATURATION_STEP = "1"
        WeeOCR.THRESH_CROP = 127.0
        WeeOCR.BLUR_MIN_VALUE = 10 //Default: 270
        WeeOCR.CAMERA_ZOOM = "18"
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
        weeOcr?.cropObject(frame, true, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT) { cropped, type, typeFrontBack ->
            if (type == CAVET || type == NONE || cropped == null || !Utils.checkSizeBitmap(cropped)) {
                processing = false
                return@cropObject
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