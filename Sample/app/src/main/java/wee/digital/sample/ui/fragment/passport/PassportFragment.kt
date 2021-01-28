package wee.digital.sample.ui.fragment.passport

import kotlinx.android.synthetic.main.passport.*
import wee.dev.weeocr.camera.CameraConfig
import wee.dev.weeocr.camera.CameraSource
import wee.dev.weeocr.camera.FrameStreamListener
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class PassportFragment : MainFragment(), FrameStreamListener {

    private var camera: CameraSource? = null

    private var complete : Boolean? = false

    override fun layoutResource(): Int = R.layout.passport

    override fun onViewCreated() {
        complete = false
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
    }

}