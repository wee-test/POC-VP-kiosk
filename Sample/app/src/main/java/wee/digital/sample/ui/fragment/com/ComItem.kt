package wee.digital.sample.ui.fragment.com

import androidx.navigation.NavDirections
import wee.digital.sample.MainDirections

data class ComItem(
        val name : String,
        val image : String,
        val navigate : NavDirections
)

const val URL_COM = "https://raw.githubusercontent.com/wee-test/POC-VP-kiosk/master/Assets/"

val listCom = listOf(
        ComItem("adv", "adv.jpg", MainDirections.actionGlobalAdvFragment()),
        ComItem("verifyFace", "face_verify.jpg", MainDirections.actionGlobalVerifyFaceFragment()),
        ComItem("home", "home.jpg", MainDirections.actionGlobalHomeFragment()),
        ComItem("document", "identify.jpg", MainDirections.actionGlobalDocumentFragment()),
        ComItem("ocr", "ocr.jpg", MainDirections.actionGlobalOcrFragment()),
        ComItem("faceRegister", "face_Register.jpg", MainDirections.actionGlobalRegisterFragment()),
        ComItem("fail", "fail.jpg", MainDirections.actionGlobalFailFragment()),
        ComItem("ocrConfirm", "ocr_input.jpg", MainDirections.actionGlobalOcrConfirmFragment()),
        ComItem("card", "card.jpg", MainDirections.actionGlobalCardFragment()),
        ComItem("review", "review.jpg", MainDirections.actionGlobalReviewFragment()),
        ComItem("loading", "loading.jpg", MainDirections.actionGlobalLoadingFragment()),
        ComItem("evaluate", "evaluate.jpg", MainDirections.actionGlobalRatingFragment()),
        ComItem("call", "call.jpg", MainDirections.actionGlobalCallFragment())
)