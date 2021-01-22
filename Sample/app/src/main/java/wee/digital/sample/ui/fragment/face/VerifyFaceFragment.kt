package wee.digital.sample.ui.fragment.face

import android.graphics.Bitmap
import kotlinx.android.synthetic.main.verify_face.*
import wee.digital.camera.RealSense
import wee.digital.camera.job.FaceCaptureJob
import wee.digital.library.extension.gone
import wee.digital.library.extension.load
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.fragment.adv.AdvVM
import wee.digital.sample.ui.main.MainFragment

class VerifyFaceFragment : MainFragment(), FaceCaptureJob.Listener {

    private val faceVM : FaceVM by lazy { viewModel(FaceVM::class) }

    private val mFaceDetectJob: FaceCaptureJob = FaceCaptureJob(this)

    private var isComplete = false

    override fun layoutResource(): Int = R.layout.verify_face

    override fun onViewCreated() {
        mFaceDetectJob.observe(viewLifecycleOwner)
    }

    override fun onLiveDataObserve() {
        RealSense.imagesLiveData.observe {
            if (isComplete) return@observe
            faceFrame?.setImageBitmap(it?.first)
        }
        faceVM.statusIdentify.observe {
            toast("$it")
        }
    }

    /**
     * [FaceCaptureJob.Listener] implement
     */
    override fun onCaptureTick(second: String?) {
        if (isComplete) return
        faceLabelTime.text = second
        faceFrameBg ?: return
        if (second != null) faceFrameBg.show() else faceFrameBg.gone()
    }

    override fun onPortraitCaptured(image: Bitmap) {
        mFaceDetectJob.pauseDetect()
        Shared.faceCapture.postValue(image)
        activity?.runOnUiThread {
            if (isComplete) return@runOnUiThread
            isComplete = true
            faceLabelStatusFace.text = "Chờ chút nhé..."
            faceFrameBg.show()
            faceFrame.setImageBitmap(image)
            navigate(MainDirections.actionGlobalHomeFragment())
        }
    }

    override fun onRecordMessage(message: String?) {
        activity?.runOnUiThread { faceLabelStatusFace.text = message }
    }

    override fun onWarningMessage(message: String) {
        activity?.runOnUiThread { faceLabelStatusFace.text = message }
    }

    override fun onCaptureTimeout() {}

    override fun onResume() {
        super.onResume()
        faceFrameBg.load(R.drawable.face_green)
        RealSense.start()
    }

    override fun onPause() {
        super.onPause()
        RealSense.imagesLiveData.postValue(null)
        RealSense.stop()
    }

}