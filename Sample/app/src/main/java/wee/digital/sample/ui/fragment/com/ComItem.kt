package wee.digital.sample.ui.fragment.com

import androidx.navigation.NavDirections
import wee.digital.sample.MainDirections

data class ComItem(
        val name : String = "",
        val image : String = "",
        val navigate : NavDirections
)

val listCom = listOf(
        ComItem("adv", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/adv.jpg", MainDirections.actionGlobalAdvFragment()),
        ComItem("verifyFace", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/face_verify.jpg", MainDirections.actionGlobalVerifyFaceFragment()),
        ComItem("home", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/home.jpg", MainDirections.actionGlobalHomeFragment()),
        ComItem("document", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/identify.jpg", MainDirections.actionGlobalDocumentFragment()),
        ComItem("ocr", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/ocr.jpg", MainDirections.actionGlobalOcrFragment()),
        ComItem("faceRegister", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/face_Register.jpg", MainDirections.actionGlobalRegisterFragment()),
        ComItem("fail", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/fail.jpg", MainDirections.actionGlobalFailFragment()),
        ComItem("ocrConfirm", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/pcr_input.jpg", MainDirections.actionGlobalOcrConfirmFragment()),
        ComItem("card", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/card.jpg", MainDirections.actionGlobalCardFragment()),
        ComItem("review", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/review.jpg", MainDirections.actionGlobalReviewFragment()),
        ComItem("loading", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/loading.jpg", MainDirections.actionGlobalLoadingFragment()),
        ComItem("evaluate", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/evaluate.jpg", MainDirections.actionGlobalComFragment()),
        ComItem("call", "https://github.com/wee-test/POC-VP-kiosk/blob/master/Assets/call.jpg", MainDirections.actionGlobalCallFragment())
)