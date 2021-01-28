package wee.digital.sample.ui.fragment.member

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_header.*
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.gone
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.activityVM
import wee.digital.sample.ui.fragment.register.RegisterVM
import wee.digital.sample.ui.main.MainFragment
import java.util.concurrent.TimeUnit

class LoadingFragment : MainFragment() {

    private val registerVM: RegisterVM by lazy { activityVM(RegisterVM::class) }

    private var disposableLoading : Disposable? = null

    override fun layoutResource(): Int = R.layout.loading

    override fun onViewCreated() {
        headerAction.gone()
    }

    private fun getDataRegister(){
        val card = Shared.ocrConfirmData.value ?: IdentifyCardInfo()
        val frameCard = if(Shared.typeCardOcr.value == Configs.TYPE_PASSPORT){
            PhotoCardInfo(
                    cardFront = Shared.passportData.value?.frame.toBytes().toStringBase64(),
                    cardBack = Shared.passportData.value?.frame.toBytes().toStringBase64()
            )
        }else{
            Shared.frameCardData.value
        }
        val cardInfo = IdentifyCardInfo(
                type = Shared.typeCardOcr.value ?: "",
                photo = frameCard,
                number = card.number,
                fullName = card.fullName,
                dateOfBirth = card.dateOfBirth,
                gender = card.gender,
                hometown = card.hometown,
                issuedDate = card.issuedDate,
                issuedPlace = card.issuedPlace,
                expiredDate = if(card.expiredDate.isNullOrEmpty()) "02/01/2025" else card.expiredDate,
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
                fullName = receiverMethod.homeInfo?.fullName ?: "",
                phoneNumber = receiverMethod.homeInfo?.phoneNumber ?: "",
                province = receiverMethod.homeInfo?.province ?: "",
                district = receiverMethod.homeInfo?.district ?: "",
                wards = receiverMethod.homeInfo?.wards ?: "",
                apartmentNumber = receiverMethod.homeInfo?.apartmentNumber ?: ""
        )
        val receiver = MethodOfReceiving(
                type = 1,
                branchCode = Shared.kioskInfo.value?.result?.kioskCode.toString(),
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
                sendSocket(false)
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            Shared.customerInfoRegisterSuccess.postValue(it)
            sendSocket(true)
            navigate(MainDirections.actionGlobalRatingFragment())
        }
    }

    private fun sendSocket(bool : Boolean){
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_7
        req?.data?.isConfirmed = bool
        Socket.action.sendData(req)
    }

    override fun onResume() {
        super.onResume()
        disposableLoading = Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getDataRegister()
                }
    }

    override fun onPause() {
        super.onPause()
        disposableLoading?.dispose()
    }

}