package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class MatchingReq(
        @SerializedName("kioskId")
        var kioskId: String = Configs.KIOSK_ID,

        @SerializedName("type")
        var type: Int = 0,

        @SerializedName("sessionId")
        var sessionId: String = "",

        @SerializedName("idCardPhoto")
        var idCardPhoto: String = "",

        @SerializedName("facePhoto")
        var facePhoto: String = ""
)