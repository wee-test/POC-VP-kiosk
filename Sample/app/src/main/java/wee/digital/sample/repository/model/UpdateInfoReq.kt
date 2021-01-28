package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class UpdateInfoReq(
        @SerializedName("videoId")
        var videoId : String = "",

        @SerializedName("customerId")
        var customerId : String = "",

        @SerializedName("transType")
        var transType : Int = 0,

        @SerializedName("kisokId")
        var kisokId : String = Configs.KIOSK_ID,

        @SerializedName("counterId")
        var counterId : String = "",

        @SerializedName("counterName")
        var counterName : String = "",

        @SerializedName("videoCallStatus")
        var videoCallStatus : Int = 0,

        @SerializedName("timeReceived")
        var timeReceived : String = "",

        @SerializedName("waitingTime")
        var waitingTime : Int = 0,

        @SerializedName("processingTime")
        var processingTime : Int = 0,

        @SerializedName("createAt")
        var createAt : String = "",
)