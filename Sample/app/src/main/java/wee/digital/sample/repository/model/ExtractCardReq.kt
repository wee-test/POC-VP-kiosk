package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class ExtractCardReq(
        @SerializedName("kioskID")
        var kioskId: String = Configs.KIOSK_ID,

        @SerializedName("photo")
        var cardImage: String = ""
)

data class FrontCardResp(
        var code: Long = -1,
        var image: ByteArray? = null,
        var address: String = "",
        var fullName: String = "",
        var number: String = "",
        var birthday: String = "",
        var expiryDate: String = "",
        var sex: String = "",
        var homeTown: String = "",
        var nationality: String = "",
        var correctAddress: String = "",
        var correctHomeTown: String = "",
        var correctName: String = ""
)

data class BackCardResp(
        var code: Long = -1,
        var image: ByteArray? = null,
        var issueDate: String = "",
        var issueBy: String = "",
        var nationality: String = "",
        var religion: String = ""
)

data class CardRespVP(
        var code : Int = -1,
        var mess : String = "",
        var id: String = "",
        var idProb: String = "",
        var name: String = "",
        var nameProb: String = "",
        var dob: String = "",
        var dobProb: String = "",
        var sex: String = "",
        var sexProb: String = "",
        var nationality: String = "",
        var nationalityProb: String = "",
        var home: String = "",
        var homeProb: String = "",
        var address: String = "",
        var addressProb: String = "",
        var typeNew: String = "",
        var doe: String = "",
        var doeProb: String = "",
        var type: String = "",
        var ward: String = "",
        var district: String = "",
        var province: String = "",
        var street: String = "",
        var ethnicityProb: String = "",
        var religion: String = "",
        var religionProb: String = "",
        var features: String = "",
        var featuresProb: String = "",
        var issueDate: String = "",
        var issueDateProb: String = "",
        var issueLoc: String = "",
        var issueLocProb: String = "",
        var passportNumber: String = "",
        var passportNumberProb: String = "",
        var idNumber: String = "",
        var idNumberProb: String = "",
        var doi: String = "",
        var doiProb: String = "",
        var checkingResult: CheckingResultResp? = null
)

data class CheckingResultResp(
        var recapturedResult: String = "",
        var checkPhotocopiedResult: String = "",
        var editedResult: String = "",
        var cornerCutResult: String = "",
        var editedProb: String = "",
        var recapturedProb: String = "",
        var checkPhotocopiedProb: String = "",
        var checkTitleResult: String = "",
        var checkEmblemResult: String = "",
        var checkEmblemProb: String = "",
        var checkFingerprintResult: String = "",
        var checkStampResult: String = "",
        var checkEmbossedStampResult: String = "",
        var checkEmbossedStampProb: String = "",
        var checkBorderResult: String = "",
        var checkBorderProb: String = ""
)