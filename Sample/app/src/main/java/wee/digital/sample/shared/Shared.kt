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

    val ocrCardInfoVP = MutableLiveData<CardRespVP>()

    val passportData = MutableLiveData<PassportData>()

    val cardSelected = MutableLiveData<CardItem>()

    val wsServer = MutableLiveData<String>()

    val wsMessage = MutableLiveData<String> ()

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

    val nationalList by lazy {
        readAsset("json/national.json").parse(Array<NationalData>::class)
    }

    fun resetData() {
        customerInfoExist.postValue(null)
        customerInfoRegisterSuccess.postValue(null)
        faceId.postValue(null)
        faceCapture.postValue(null)
        typeCardOcr.postValue(null)
        messageFail.postValue(null)
        frameCardData.postValue(null)
        ocrConfirmData.postValue(null)
        methodReceiveCard.postValue(null)
        ocrCardFront.postValue(null)
        ocrCardBack.postValue(null)
        passportData.postValue(null)
        cardSelected.postValue(null)
        socketStatusConnect.postValue(null)
        socketReqData.postValue(null)
        callVideo.postValue("")
        ocrCardInfoVP.postValue(null)
        Configs.isMute = false
        dataCallLog = null
    }

}