package wee.digital.sample.ui.fragment.register

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.android.synthetic.main.register.*
import wee.digital.camera.RealSense
import wee.digital.camera.job.FaceCaptureJob
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
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class RegisterFragment : MainFragment(), FaceCaptureJob.Listener {

    private val registerVM : RegisterVM by lazy { viewModel(RegisterVM::class) }

    private val mFaceDetectJob: FaceCaptureJob = FaceCaptureJob(this)

    private var isComplete = false

    override fun layoutResource(): Int {
        return R.layout.register
    }

    override fun onViewCreated() {
        mFaceDetectJob.observe(viewLifecycleOwner)
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
            sendSocket(true)
            Shared.faceId.postValue(it.validateResult.faceID)
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
        if(isComplete) return
        registerLabelTime.text = second
        registerFrameBg ?: return
        if (second != null) registerFrameBg.show() else registerFrameBg.gone()
    }

    override fun onPortraitCaptured(image: Bitmap) {
        mFaceDetectJob.pauseDetect()
        Shared.faceCapture.postValue(image)
        activity?.runOnUiThread {
            if (isComplete) return@runOnUiThread
            isComplete = true
            registerStatusFace.text = "Chờ chút nhé..."
            registerFrameBg.show()
            registerFrame.setImageBitmap(image)
            if(Shared.typeCardOcr.value == Configs.TYPE_PASSPORT){
                registerVM.verifyIdCard(Shared.passportData.value?.frame.toBytes().toStringBase64(), image.toBytes())
            }else{
                registerVM.verifyIdCard(Shared.frameCardData.value?.cardFront ?: "", image.toBytes())
            }
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
        RealSense.imagesLiveData.postValue(null)
        RealSense.stop()
    }

}