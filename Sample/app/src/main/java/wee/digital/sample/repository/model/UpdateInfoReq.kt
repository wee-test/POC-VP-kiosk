package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class UpdateInfoReq(
        @SerializedName("kioskId")
        var kioskId : String = Configs.KIOSK_ID,

        @SerializedName("videoId")
        var videoId : String = "",

        @SerializedName("customerId")
        var customerId : String = "",

        @SerializedName("transType")
        var transType : Int = 0,

        @SerializedName("counterId")
        var counterId : String = "",

        @SerializedName("videoCallStatus")
        var videoCallStatus : Int = 0,

        @SerializedName("timeReceived")
        var timeReceived : String = "",

        @SerializedName("waitingTime")
        var waitingTime : Int = 0,

        @SerializedName("processingTime")
        var processingTime : Int = 0
)