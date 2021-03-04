package wee.digital.sample.ui.fragment.member

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.loading.*
import kotlinx.android.synthetic.main.view_header.*
import vplib.RegisterResult
import vplib.ResponseCode
import vplib.ResponseCustomerRegister
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.gone
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.activityVM
import wee.digital.sample.ui.fragment.register.RegisterVM
import wee.digital.sample.ui.main.MainActivity
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import java.lang.Exception
import java.util.concurrent.TimeUnit

class LoadingFragment : MainFragment() {

    private val registerVM: RegisterVM by lazy { activityVM(RegisterVM::class) }

    private var disposableLoading: Disposable? = null

    override fun layoutResource(): Int = R.layout.loading

    override fun onViewCreated() {
        headerAction.gone()
        Shared.cardSelected.observe {
            loadingCard.setImageResource(it.image)
        }
    }

    private fun getDataRegister() {
        val card = Shared.ocrConfirmData.value ?: IdentifyCardInfo()
        val frameCard = if (Shared.typeCardOcr.value == Configs.TYPE_PASSPORT) {
            PhotoCardInfo(
                    cardFront = Shared.passportData.value?.frame.toBytes().toStringBase64(),
                    cardBack = Shared.passportData.value?.frame.toBytes().toStringBase64()
            )
        } else {
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
                passportNumber = card.passportNumber,
                passportType = card.passportType,
                passportCode = card.passportCode,
                expiredDate = if (card.expiredDate.isEmpty()) "02/01/2025" else card.expiredDate,
                permanentAddress = card.permanentAddress,
                nationality = "Việt Nam"
        )
        val infoCustomer = CustomerInfoRegister(
                customerType = 1,
                identityCardInfo = cardInfo,
                phoneNumber = card.phone,
                email = card.email,
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
                videoCallId = Shared.sessionVideo.value?.result?.videoCallID.toString().replace("null", ""),
                methodOfReceiving = receiver
        )
        Voice.ins?.request(VoiceData.PROCESSING_SCREEN){
            registerVM.registerCard(body)
            Log.e("registerCard", "$body")
        }
    }

    override fun onLiveDataObserve() {
        registerVM.statusRegisterCard.observe {
            Log.e("registerCard", "$it")
            if(it == null || it?.responseCode?.code ?: -1 != 0L){
                if (!getStatusApi()) {
                    val accountNumber = Utils.randomAccountNumber()
                    val respRegister = ResponseCustomerRegister()
                    val resultRegister = RegisterResult()
                    resultRegister.accountNumber = accountNumber
                    resultRegister.customerID = "000001"
                    respRegister.result = resultRegister
                    respRegister.responseCode = ResponseCode().also { it.code = -1L }
                    Shared.customerInfoRegisterSuccess.postValue(respRegister)
                    printCard("9704${accountNumber}")
                    sendSocket(true)
                    navigate(MainDirections.actionGlobalRatingFragment())
                    return@observe
                }
                Shared.messageFail.postValue(
                        MessageData("Đăng ký mở thẻ thất bại",
                                "Có lỗi xảy ra trong quá trình mở thẻ, bạn vui lòng thử lại")
                )
                sendSocket(false)
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            }
            printCard(it.result.cardNumber)
            Shared.customerInfoRegisterSuccess.postValue(it)
            sendSocket(true)
            navigate(MainDirections.actionGlobalRatingFragment())
        }
    }

    private fun sendSocket(bool: Boolean) {
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_7
        req?.data?.isConfirmed = bool
        Socket.action.sendData(req)
        Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
    }

    private fun printCard(card: String) {
        val mainActivity = activity as? MainActivity ?: return
        if (Shared.methodReceiveCard.value?.type == 1) {
            mainActivity.printCard(
                    Utils.spaceAccountNumber(card),
                    Shared.ocrConfirmData.value?.fullName ?: "Nguyen Van A",
                    "03/07"
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val time = if (Shared.methodReceiveCard.value?.type == 1) 20 else 2
        disposableLoading = Observable.timer(time.toLong(), TimeUnit.SECONDS)
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