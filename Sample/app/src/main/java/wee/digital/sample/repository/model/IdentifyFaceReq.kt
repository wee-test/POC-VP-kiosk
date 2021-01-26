package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class IdentifyFaceReq(
        @SerializedName("kioskID")
        var kioskId : String = Configs.KIOSK_ID,

        @SerializedName("photo")
        var faceImage : String = ""
)