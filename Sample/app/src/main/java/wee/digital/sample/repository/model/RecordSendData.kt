package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName

class RecordSendData (
        @SerializedName("videoCallId")
        var videoCallId : String = "",

        @SerializedName("Ekycid")
        var Ekycid : String = "",

        @SerializedName("body")
        var body : ByteArray? = null
)