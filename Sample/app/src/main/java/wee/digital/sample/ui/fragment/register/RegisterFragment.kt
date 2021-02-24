package wee.digital.sample.ui.fragment.register

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.android.synthetic.main.register.*
import wee.digital.camera.RealSense
import wee.digital.camera.job.DetectionJob
import wee.digital.camera.job.FaceCaptureJob
import wee.digital.camera.job.FaceDetectJob
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.gone
import wee.digital.library.extension.load
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.MessageData
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.fragment.ApiVM
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class RegisterFragment : MainFragment(), FaceCaptureJob.Listener {

    private val registerVM : RegisterVM by lazy { viewModel(RegisterVM::class) }

    private var mFaceDetectJob: DetectionJob? = null

    private var faceBitmap : Bitmap? = null

    private var isComplete = false

    override fun layoutResource(): Int {
        return R.layout.register
    }

    override fun onViewCreated() {
        RealSense.isVerifyFace = false
        isComplete = false
        mFaceDetectJob = DetectionJob(this)
        mFaceDetectJob?.observe(viewLifecycleOwner)
        RealSense.imagesLiveData.postValue(null)
        Voice.ins?.request(VoiceData.FACE_REGISTER)
    }

    override fun onLiveDataObserve() {
        RealSense.imagesLiveData.observe {
            if (isComplete) return@observe
            registerFrame?.setImageBitmap(it?.first)
        }
        registerVM.statusVerifyCard.observe {
            if(it == null || it?.responseCode?.code ?: -1 != 0L || !it.isMatched){
                val messFail = MessageData(
                        "Đăng ký không thành công",
                        VoiceData.FACE_NOT_MATCHED
                )
                sendSocket(false)
                Shared.messageFail.postValue(messFail)
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            registerVM.matchingFrame(Shared.frameCardData.value?.cardFront ?: "", faceBitmap.toBytes())
            Shared.faceId.postValue(it.validateResult.faceID)
        }
        registerVM.statusMatching.observe {
            if(it == null || it.responseCode?.code ?: -1 != 0L){
                val messFail = MessageData(
                        "Đăng ký không thành công",
                        VoiceData.FACE_NOT_MATCHED
                )
                sendSocket(false)
                Shared.messageFail.postValue(messFail)
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            sendSocket(true)
            registerVM.createVideo(requireContext(), Shared.faceCapture.value?.toBytes().toStringBase64())
            navigate(MainDirections.actionGlobalCardFragment())
        }
    }

    private fun sendSocket(isMatch: Boolean) {
        val resp = Shared.socketReqData.value
        resp?.cmd = Configs.FORM_STEP_4
        resp?.data?.idCardMatched = isMatch
        resp?.data?.faceImage = Base64.encodeToString(Shared.faceCapture.value.toBytes(), Base64.NO_WRAP)
        Socket.action.sendData(resp)
        Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
    }

    /**
     * [FaceCaptureJob.Listener] implement
     */
    override fun onCaptureTick(second: String?) {
        activity?.runOnUiThread {
            if(isComplete) return@runOnUiThread
            registerLabelTime.text = second
            registerFrameBg ?: return@runOnUiThread
            if (second != null) registerFrameBg.show() else registerFrameBg.gone()
        }
    }

    override fun onPortraitCaptured(image: Bitmap) {
        mFaceDetectJob?.pauseDetect()
        Shared.faceCapture.postValue(image)
        activity?.runOnUiThread {
            if (isComplete) return@runOnUiThread
            isComplete = true
            faceBitmap = image
            registerStatusFace.text = "Chờ chút nhé..."
            registerFrameBg.show()
            registerFrame.setImageBitmap(image)
            registerVM.verifyIdCard(Shared.frameCardData.value?.cardFront ?: "", image.toBytes())
        }
    }

    override fun onRecordMessage(message: String?) {
        activity?.runOnUiThread { registerStatusFace.text = message }
    }

    override fun onWarningMessage(message: String) {
        activity?.runOnUiThread { registerStatusFace.text = message }
    }

    override fun onCaptureTimeout() {}

    override fun onResume() {
        super.onResume()
        registerFrameBg.load(R.drawable.face_white)
        RealSense.start()
    }

    override fun onPause() {
        super.onPause()
        RealSense.stop()
    }

}