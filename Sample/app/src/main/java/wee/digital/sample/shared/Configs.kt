package wee.digital.sample.shared

import wee.dev.weeocr.WeeOCR
import java.text.SimpleDateFormat


object Configs {

    const val KIOSK_CODE = "KIOSK-DEV"

    var KIOSK_ID = ""

    const val SOCKET_URL = "wss://vpbank.wee.vn/ws"

    const val TYPE_NID = "CMND"

    const val TYPE_NID_12 = "CMND12"

    const val TYPE_CCCD = "CCCD"

    const val TYPE_PASSPORT = "PASSPORT"

    const val DEFAULT_ARG_KEY: String = "default"

    val DEFAULT_DATE_FMT = SimpleDateFormat("dd/MM/yyyy")

    val DEFAULT_DATE_TIME_FMT = SimpleDateFormat("HH:mm dd/MM/yyyy")

    val API_DATE_FMT = SimpleDateFormat("yyyy-MM-dd")

    var isMute = false

    const val FORM_STEP_1 = "FORM-STEP-1"
    const val FORM_STEP_2 = "FORM-STEP-2"
    const val FORM_STEP_3 = "FORM-STEP-3"
    const val FORM_STEP_4 = "FORM-STEP-4"
    const val FORM_STEP_5 = "FORM-STEP-5"
    const val FORM_STEP_6 = "FORM-STEP-6"
    const val FORM_STEP_7 = "FORM-STEP-7"
    const val FORM_STEP_8 = "FORM-STEP-8"
    const val END_STEP = "END-STEP"

    const val ID_CARD_FRONT = 1
    const val ID_CARD_BACK = 2

    fun configWeeOcr(){
        WeeOCR.CAMERA_ID = 1
        WeeOCR.CAMERA_SATURATION_STEP = -2
        WeeOCR.THRESH_CROP = 64.0
        WeeOCR.BLUR_MIN_VALUE = 80
        WeeOCR.CAMERA_ZOOM = "20"
        WeeOCR.DELAY_SCAN = 5
        WeeOCR.DOWNSCALE_IMAGE_SIZE_TEMPLATE = 480.0
    }

}
