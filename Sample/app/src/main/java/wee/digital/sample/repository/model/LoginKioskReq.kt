package wee.digital.sample.repository.model

import com.google.gson.annotations.SerializedName
import wee.digital.sample.shared.Configs

data class LoginKioskReq(
        @SerializedName("kioskCode")
        var kioskCode: String = Configs.KIOSK_ID
)