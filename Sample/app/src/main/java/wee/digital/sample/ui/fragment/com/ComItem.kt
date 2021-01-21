package wee.digital.sample.ui.fragment.com

import androidx.navigation.NavDirections
import wee.digital.sample.MainDirections

data class ComItem(
        val name : String = "",
        val image : String = "",
        val navigate : NavDirections
)

const val URL_COM = "https://raw.githubusercontent.com/wee-test/POC-VP-kiosk/master/"

val listCom = listOf(
        ComItem("adv", "Assets/adv.jpg", MainDirections.actionGlobalAdvFragment()),
        ComItem("verifyFace", "Assets/face_verify.jpg", MainDirections.actionGlobalVerifyFaceFragment()),
        ComItem("home", "Assets/home.jpg", MainDirections.actionGlobalHomeFragment()),
        ComItem("document", "Assets/identify.jpg", MainDirections.actionGlobalDocumentFragment()),
        ComItem("ocr", "Assets/ocr.jpg", MainDirections.actionGlobalOcrFragment()),
        ComItem("faceRegister", "Assets/face_Register.jpg", MainDirections.actionGlobalRegisterFragment()),
        ComItem("fail", "Assets/fail.jpg", MainDirections.actionGlobalFailFragment()),
        ComItem("ocrConfirm", "Assets/ocr_input.jpg", MainDirections.actionGlobalOcrConfirmFragment()),
        ComItem("card", "Assets/card.jpg", MainDirections.actionGlobalCardFragment()),
        ComItem("review", "Assets/review.jpg", MainDirections.actionGlobalReviewFragment()),
        ComItem("loading", "Assets/loading.jpg", MainDirections.actionGlobalLoadingFragment()),
        ComItem("evaluate", "Assets/evaluate.jpg", MainDirections.actionGlobalComFragment()),
        ComItem("call", "Assets/call.jpg", MainDirections.actionGlobalCallFragment())
)