package wee.digital.sample.ui.fragment.ocr

import kotlinx.android.synthetic.main.ocr.*
import wee.dev.weeocr.camera.CameraConfig
import wee.dev.weeocr.camera.CameraSource
import wee.dev.weeocr.camera.FrameStreamListener
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment
import java.lang.Exception

class OcrFragment : MainFragment(), FrameStreamListener {

    private var mCamera : CameraSource? = null

    override fun layoutResource(): Int = R.layout.ocr

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        stopCamera()
    }

    private fun startCamera(){
        try{
            mCamera = CameraSource(requireActivity(), ocrGraphicOverlay).also {
                it.setFacing(CameraConfig.facing)
                ocrPreview.start(it, ocrGraphicOverlay)
                it.setFrameProcessorListener(this)
            }
        }catch (e : Exception){
            toast("start camera error : ${e.message}")
        }
    }

    private fun stopCamera(){
        try{
            mCamera?.release()
            ocrPreview?.release()
            mCamera = null
        }catch (e : Exception){
            toast("stop camera error : ${e.message}")
        }
    }

    override fun onFrame(byteArray: ByteArray) {

    }

}