package wee.digital.sample.ui.fragment.face

import android.graphics.Bitmap
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.verify_face.*
import vplib.CustomerInfo
import vplib.IdentityCardInfo
import vplib.ResponseGetCustomerInfo
import wee.dev.weewebrtc.utils.extension.toObject
import wee.digital.camera.RealSense
import wee.digital.camera.job.FaceCaptureJob
import wee.digital.camera.resize
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.MessageData
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class VerifyFaceFragment : MainFragment(), FaceCaptureJob.Listener {

    private val faceVM : FaceVM by lazy { viewModel(FaceVM::class) }

    private var mFaceDetectJob: FaceCaptureJob? = null

    private var isComplete = false

    override fun layoutResource(): Int = R.layout.verify_face

    override fun onViewCreated() {
        isComplete = false
        RealSense.isVerifyFace = true
        RealSense.imagesLiveData.postValue(null)
        mFaceDetectJob = FaceCaptureJob(this)
        mFaceDetectJob?.observe(viewLifecycleOwner)
        Voice.ins?.request(VoiceData.FACE_ENROLL)
    }

    override fun onLiveDataObserve() {
        RealSense.imagesLiveData.observe {
            if (isComplete) return@observe
            faceFrame?.setImageBitmap(it?.first)
        }
        faceVM.statusIdentify.observe {
            if (it == null || it.responseCode?.code ?: -1 != 0L) {
                Shared.messageFail.postValue(
                        MessageData("Xác thực khuôn mặt thất bại", "Bạn vui lòng thực hiện lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            val arrCustomer = it.customerListString.parse(JsonArray::class) ?: JsonArray()
            if (arrCustomer.isEmpty()) {
                navigate(MainDirections.actionGlobalHomeFragment())
            } else {
                faceVM.getInfoCustomer(arrCustomer[0].toObject()?.get("customerId")?.asString ?: "")
            }
        }
        faceVM.statusInfoCustomer.observe {
            if (it.responseCode.code != 0L) {
                Shared.messageFail.postValue(
                        MessageData("Hệ thống bị lỗi",
                                "Bạn vui lòng thử lại trong ít phút")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.customerInfoExist.postValue(it)
            navigate(MainDirections.actionGlobalCustomerExistFragment())
        }
        faceVM.statusSearchVP.observe {
            val infoCard = IdentityCardInfo()
            if (it == null || it.responseCode.code != 0L) {
                infoCard.fullName = ""
            } else {
                infoCard.fullName = it.result.data.fullName
            }
            val customer = CustomerInfo()
            customer.identityCardInfo = infoCard
            val respCustomerExist = ResponseGetCustomerInfo()
            respCustomerExist.customerInfo = customer
            Shared.customerInfoExist.postValue(respCustomerExist)
            navigate(MainDirections.actionGlobalHomeFragment())
        }
    }

    /**
     * [FaceCaptureJob.Listener] implement
     */
    override fun onCaptureTick(second: String?) {
        view?.post {
            if (isComplete) return@post
            faceLabelTime.text = second
            faceFrameBg ?: return@post
            if (second != null) faceFrameBg.show() else faceFrameBg.gone()
        }
    }

    override fun onPortraitCaptured(image: Bitmap) {
        mFaceDetectJob?.pauseDetect()
        Shared.faceCapture.postValue(image)
        view?.post {
            if (isComplete) return@post
            isComplete = true
            faceLabelStatusFace.text = "Chờ chút nhé..."
            faceFrameBg.show()
            faceFrame.setImageBitmap(image)
            if (getStatusApi()) {
                faceVM.identifyFace(image.toBytes())
            } else {
                faceVM.searchCustomer(image.toBytes().toStringBase64())
            }
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
        RealSense.stop()
    }

}