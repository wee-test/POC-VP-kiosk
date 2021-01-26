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
        var code : Long = -1,
        var image: ByteArray? = null,
        var address: String = "",
        var fullName: String = "",
        var number: String = "",
        var birthday: String = "",
        var expiryDate: String = "",
        var sex: String = "",
        var homeTown: String = "",
        var nationality : String = "",
        var correctAddress : String = "",
        var correctHomeTown : String = "",
        var correctName : String = ""
)

data class BackCardResp(
        var code : Long = -1,
        var image : ByteArray? = null,
        var issueDate : String = "",
        var issueBy : String = "",
        var nationality : String = "",
        var religion : String = ""
)