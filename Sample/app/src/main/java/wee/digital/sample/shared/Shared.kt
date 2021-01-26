package wee.digital.sample.shared

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import vplib.ResponseFaceIdentity
import vplib.ResponseGetCustomerInfo
import vplib.ResponseLogin
import wee.digital.library.extension.parse
import wee.digital.library.extension.readAsset
import wee.digital.sample.R
import wee.digital.sample.repository.model.*
import wee.digital.sample.ui.fragment.card.CardItem
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable

object Shared {

    val listAdv = listOf(R.mipmap.adv1, R.mipmap.adv2)

    val kioskInfo = MutableLiveData<ResponseLogin>()

    val customerInfoVerify = MutableLiveData<ResponseFaceIdentity>()

    val customerInfoExist = MutableLiveData<ResponseGetCustomerInfo>()

    val faceCapture = MutableLiveData<Bitmap>()

    val typeCardOcr = MutableLiveData<String>()

    val messageFail = MutableLiveData<MessageData>()

    val frameCardData = MutableLiveData<PhotoCardInfo>()

    val ocrConfirmData = MutableLiveData<IdentifyCardInfo>()

    val methodReceiveCard = MutableLiveData<MethodOfReceiving>()

    val ocrCardFront = MutableLiveData<FrontCardResp>()

    val ocrCardBack = MutableLiveData<BackCardResp>()

    val cardSelected = MutableLiveData<CardItem>()

    val socketReqData = MutableLiveData<SocketReq>()

    val branchList by lazy {
        readAsset("branch_list.json").parse(Array<Selectable>::class)
    }

    val provinceList by lazy {
        readAsset("province_list.json").parse(Array<Selectable>::class)
    }

    val genderList by lazy {
        readAsset("gender.json").parse(Array<Selectable>::class)
    }

}