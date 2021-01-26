package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class VerifyIdCardReq(
        @SerializedName("kioskID")
        var kioskId: String = Configs.KIOSK_ID,

        @SerializedName("idCardPhoto")
        var cardImage: String = "",

        @SerializedName("facePhoto")
        var faceImage: String = ""
)