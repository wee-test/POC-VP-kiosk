package wee.digital.sample.ui.fragment.register

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.register.*
import wee.digital.camera.RealSense
import wee.digital.camera.job.FaceCaptureJob
import wee.digital.library.extension.gone
import wee.digital.library.extension.show
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment

class RegisterFragment : MainFragment(), FaceCaptureJob.Listener {

    private val registerVM : RegisterVM by lazy { viewModel(RegisterVM::class) }

    private val mFaceDetectJob: FaceCaptureJob = FaceCaptureJob(this)

    private var isComplete = false

    override fun layoutResource(): Int {
        return R.layout.register
    }

    override fun onViewCreated() {
        mFaceDetectJob.observe(viewLifecycleOwner)
    }

    override fun onLiveDataObserve() {
        RealSense.imagesLiveData.observe {
            if (isComplete) return@observe
            registerFrame?.setImageBitmap(it?.first)
        }
    }

    /**
     * [FaceCaptureJob.Listener] implement
     */
    override fun onCaptureTick(second: String?) {
        registerLabelTime.text = second
        registerFrameBg ?: return
        if (second != null) registerFrameBg.show() else registerFrameBg.gone()
    }

    override fun onPortraitCaptured(image: Bitmap) {
        mFaceDetectJob.pauseDetect()
        Shared.faceCapture.postValue(image)
        activity?.runOnUiThread {
            registerStatusFace.text = "Chờ chút nhé..."
            if (isComplete) return@runOnUiThread
            isComplete = true
            registerFrame.setImageBitmap(image)
            navigate(MainDirections.actionGlobalHomeFragment())
        }
    }

    override fun onRecordMessage(message: String?) {
        activity?.runOnUiThread { registerStatusFace.text = message }
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