package wee.digital.sample.repository.model

import wee.digital.sample.BuildConfig

class VPKioskInfo (
        val kioskVersionCode: String = "${BuildConfig.VERSION_CODE}",
        val kioskVersionName: String = "${BuildConfig.VERSION_NAME}",
        var check: Boolean = false,
        var time: String = ""
)