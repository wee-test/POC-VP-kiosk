package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class VerifyFaceReq(
        @SerializedName("kioskID")
        var kioskId : String = Configs.KIOSK_ID,

        @SerializedName("customerID")
        var customerID : String = "",

        @SerializedName("photo")
        var faceImage : String = ""
)