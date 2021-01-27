package wee.digital.sample.ui.fragment.member

import com.google.gson.Gson
import kotlinx.android.synthetic.main.loading.*
import kotlinx.android.synthetic.main.view_header.*
import wee.dev.weewebrtc.utils.extension.toObject
import wee.digital.library.extension.gone
import wee.digital.library.extension.str
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.activityVM
import wee.digital.sample.ui.fragment.register.RegisterVM
import wee.digital.sample.ui.main.MainFragment

class LoadingFragment : MainFragment() {

    private val registerVM: RegisterVM by lazy { activityVM(RegisterVM::class) }

    override fun layoutResource(): Int = R.layout.loading

    override fun onViewCreated() {
        headerAction.gone()
        val card = Shared.ocrConfirmData.value ?: IdentifyCardInfo()
        val cardInfo = IdentifyCardInfo(
                type = Shared.typeCardOcr.value ?: "",
                photo = Shared.frameCardData.value,
                number = card.number,
                fullName = card.fullName,
                dateOfBirth = card.dateOfBirth,
                gender = card.gender,
                hometown = card.hometown,
                issuedDate = card.issuedDate,
                issuedPlace = card.issuedPlace,
                expiredDate = "29/10/2025"/*card.expiredDate*/,
                permanentAddress = card.permanentAddress,
                nationality = "Việt Nam"
        )
        val infoCustomer = CustomerInfoRegister(
                customerType = 1,
                identityCardInfo = cardInfo,
                phoneNumber = card.phone,
                createAt = "${System.currentTimeMillis()}"
        )

        val receiverMethod = Shared.methodReceiveCard.value ?: MethodOfReceiving()
        val infoHome = HomeInfo(
                fullName = /*receiverMethod.homeInfo?.fullName ?:*/ "Nguyen Van A",
                phoneNumber = /*receiverMethod.homeInfo?.phoneNumber ?:*/ "0865971677",
                province = /*receiverMethod.homeInfo?.province ?:*/ "Thanh Pho Ho Chi Minh",
                district = "Quan Go Vap",
                wards = "bach dang",
                apartmentNumber = "b20"
        )
        val receiver = MethodOfReceiving(
                type = 1,
                branchCode = "BR001",
                homeInfo = infoHome
        )
        val body = CustomerRegisterReq(
                faceId = Shared.faceId.value ?: "",
                customerInfo = infoCustomer,
                cardType = Shared.cardSelected.value?.type ?: "PLATINUM_CASHBACK",
                ekycType = 1,
                videoCallId = Shared.sessionVideo.value?.result?.videoCallID.toString(),
                methodOfReceiving = receiver
        )
        registerVM.registerCard(body)
    }

    override fun onLiveDataObserve() {
        registerVM.statusRegisterCard.observe {
            if(it == null || it?.responseCode?.code ?: -1 != 0L){
                Shared.messageFail.postValue(
                        MessageData("Đăng ký mở thẻ thất bại",
                                "Có Clỗi xảy ra trong quá trình mở thẻ, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.customerInfoRegisterSuccess.postValue(it)
            sendSocket()
            navigate(MainDirections.actionGlobalRatingFragment())
        }
    }

    private fun sendSocket(){
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_7
        req?.data?.isConfirmed = true
        Socket.action.sendData(req)
    }

}