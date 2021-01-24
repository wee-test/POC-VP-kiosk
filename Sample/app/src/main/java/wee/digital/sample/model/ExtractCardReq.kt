package wee.digital.sample.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class ExtractCardReq(
        @SerializedName("kioskID")
        var kioskId: String = Configs.KIOSK_ID,

        @SerializedName("photo")
        var cardImage: String = ""
)