package wee.digital.sample.shared

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import vplib.*
import wee.dev.weeocr.repository.model.PassportData
import wee.dev.weewebrtc.repository.model.CallLog
import wee.digital.library.extension.parse
import wee.digital.library.extension.readAsset
import wee.digital.sample.repository.model.*
import wee.digital.sample.repository.model.MethodOfReceiving
import wee.digital.sample.ui.base.EventLiveData
import wee.digital.sample.ui.fragment.card.CardItem
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable

object Shared {

    val kioskInfo = MutableLiveData<ResponseLogin>()

    val sessionVideo = MutableLiveData<ResponseVideoCallCreateSession>()

    val customerInfoVerify = MutableLiveData<ResponseFaceIdentity>()

    val customerInfoExist = MutableLiveData<ResponseGetCustomerInfo>()

    val customerInfoRegisterSuccess = MutableLiveData<ResponseCustomerRegister>()

    val faceId = MutableLiveData<String>()

    val faceCapture = MutableLiveData<Bitmap>()

    val typeCardOcr = MutableLiveData<String>()

    val messageFail = MutableLiveData<MessageData>()

    val frameCardData = MutableLiveData<PhotoCardInfo>()

    val ocrConfirmData = MutableLiveData<IdentifyCardInfo>()

    val methodReceiveCard = MutableLiveData<MethodOfReceiving>()

    val ocrCardFront = MutableLiveData<FrontCardResp>()

    val ocrCardBack = MutableLiveData<BackCardResp>()

    val passportData = MutableLiveData<PassportData>()

    val cardSelected = MutableLiveData<CardItem>()

    /**
     * giao dich vien
     */
    val socketReqData = MutableLiveData<SocketReq>()

    val socketStatusConnect = EventLiveData<ResponseTellerContact>()

    val callVideo = EventLiveData<String>()

    var dataCallLog : CallLog? = null

    /**
     * list json
     */

    val provinceList by lazy {
        readAsset("json/province.json").parse(Array<Selectable>::class)
    }

    val districtList by lazy {
        readAsset("json/district.json").parse(Array<Selectable>::class)
    }

    val wardList by lazy {
        readAsset("json/ward.json").parse(Array<Selectable>::class)
    }

    val genderList by lazy {
        readAsset("json/gender.json").parse(Array<Selectable>::class)
    }

}