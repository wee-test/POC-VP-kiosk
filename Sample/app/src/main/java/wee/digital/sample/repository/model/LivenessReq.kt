package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class LivenessReq(
        @SerializedName("kioskId")
        var kioskId: String = Configs.KIOSK_ID,

        @SerializedName("type")
        var type: Int = 0,

        @SerializedName("sessionId")
        var sessionId: String = "",

        @SerializedName("facePhoto")
        var face: String = "",

        @SerializedName("livenessVideo")
        var livenessVideo: String = ""
)