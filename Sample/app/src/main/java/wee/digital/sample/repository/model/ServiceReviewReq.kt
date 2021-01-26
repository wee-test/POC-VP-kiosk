package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class ServiceReviewReq(
        @SerializedName("kioskId")
        var kioskId : String = Configs.KIOSK_ID,

        @SerializedName("customerId")
        var customerId : String = "",

        @SerializedName("transId")
        var transId : String = "",

        @SerializedName("reviewType")
        var reviewType : Int = -1
)