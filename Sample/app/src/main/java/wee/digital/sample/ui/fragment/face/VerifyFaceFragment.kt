package wee.digital.sample.ui.fragment.face

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.fragment_verify_face.*
import wee.digital.camera.RealSense
import wee.digital.camera.job.FaceCaptureJob
import wee.digital.library.extension.gone
import wee.digital.library.extension.load
import wee.digital.library.extension.show
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class VerifyFaceFragment : MainFragment(), FaceCaptureJob.Listener {

    private val mFaceDetectJob: FaceCaptureJob = FaceCaptureJob(this)

    private var isComplete = false

    override fun layoutResource(): Int = R.layout.fragment_verify_face

    override fun onViewCreated() {
        mFaceDetectJob.observe(viewLifecycleOwner)
    }

    override fun onLiveDataObserve() {
        RealSense.imagesLiveData.observe {
            if (isComplete) return@observe
            faceFrame?.setImageBitmap(it?.first)
        }
    }

    /**
     * [FaceCaptureJob.Listener] implement
     */
    override fun onCaptureTick(second: String?) {
        faceLabelTime.text = second
        faceFrameBg ?: return
        if (second != null) faceFrameBg.show() else faceFrameBg.gone()
    }

    override fun onPortraitCaptured(image: Bitmap) {
        mFaceDetectJob.pauseDetect()
        activity?.runOnUiThread {
            faceLabelStatusFace.text = "Chờ chút nhé..."
            if (isComplete) return@runOnUiThread
            isComplete = true
            faceFrame.setImageBitmap(image)
        }
    }

    override fun onRecordMessage(message: String?) {
        activity?.runOnUiThread { faceLabelStatusFace.text = message }
    }

    override fun onCaptureTimeout() {}

    override fun onResume() {
        super.onResume()
        RealSense.start()
    }

    override fun onPause() {
        super.onPause()
        RealSense.imagesLiveData.postValue(null)
        RealSense.stop()
    }

}