package wee.digital.sample.ui.fragment.member

import kotlinx.android.synthetic.main.view_header.*
import wee.dev.weewebrtc.utils.extension.toObject
import wee.digital.library.extension.gone
import wee.digital.library.extension.str
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
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
                expiredDate = card.expiredDate,
                nationality = "Việt Nam"
        )
        val infoCustomer = CustomerInfoRegister(
                customerID = "",
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
                district = "Quan Go Vap",
                wards = "bach dang",
                apartmentNumber = "b20"
        )
        val receiver = MethodOfReceiving(
                type = 1,
                branchCode = "12",
                homeInfo = infoHome
        )
        val body = CustomerRegisterReq(
                customerInfo = infoCustomer,
                cardType = Shared.cardSelected.value?.type ?: "",
                ekycType = 1,
                methodOfReceiving = receiver
        )
        registerVM.registerCard(body)
    }

    override fun onLiveDataObserve() {
        registerVM.statusRegisterCard.observe {
            toast("$it")
        }
    }

}