package wee.digital.sample.ui.fragment.passport

import android.graphics.Camera
import kotlinx.android.synthetic.main.passport.*
import wee.dev.weeocr.WeeOCR
import wee.dev.weeocr.camera.CameraConfig
import wee.dev.weeocr.camera.CameraSource
import wee.dev.weeocr.camera.FrameStreamListener
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class PassportFragment : MainFragment(), FrameStreamListener {

    private var camera: CameraSource? = null

    private var complete: Boolean? = false

    private var weeOcr: WeeOCR? = null

    private var processing = false

    override fun layoutResource(): Int = R.layout.passport

    override fun onViewCreated() {
        complete = false
        processing = false
        WeeOCR.CAMERA_ID = 1
        WeeOCR.CAMERA_SATURATION_STEP = "0"
        WeeOCR.THRESH_CROP = 64.0
        WeeOCR.BLUR_MIN_VALUE = 100
        WeeOCR.CAMERA_ZOOM = "18"
        WeeOCR.DELAY_SCAN = 10
        WeeOCR.DOWNSCALE_IMAGE_SIZE_TEMPLATE = 960.0
        weeOcr = WeeOCR(requireActivity())
    }

    override fun onLiveDataObserve() {
    }

    private fun startCamera() {
        try {
            camera = CameraSource(requireActivity(), passportGraphicOverlay).also {
                it.setFacing(CameraConfig.facing)
                passportPreview.start(it, passportGraphicOverlay)
                it.setFrameProcessorListener(this)
            }
        } catch (e: Exception) {
            toast("start camera error : ${e.message}")
        }
    }

    private fun release() {
        try {
            camera?.release()
            passportPreview?.release()
            weeOcr?.destroy()
            camera = null
        } catch (e: Exception) {
            toast("stop camera error : ${e.message}")
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

    /**
     * listener frame camera
     */
    override fun onFrame(byteArray: ByteArray) {
        if (processing) return
        processing = true
        weeOcr?.checkPassport(byteArray, CameraConfig.CAMERA_WIDTH, CameraConfig.CAMERA_HEIGHT) {
            if (it.frame != null) {
                Shared.passportData.postValue(it)
                navigate(MainDirections.actionGlobalOcrConfirmFragment())
                return@checkPassport
            }
            processing = false
        }
    }

}