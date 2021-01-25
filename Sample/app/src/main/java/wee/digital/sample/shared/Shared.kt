package wee.digital.sample.shared

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import wee.digital.library.extension.parse
import wee.digital.library.extension.readAsset
import wee.digital.sample.R
import wee.digital.sample.model.IdentifyCardInfo
import wee.digital.sample.model.MessageData
import wee.digital.sample.model.MethodOfReceiving
import wee.digital.sample.model.PhotoCardInfo
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable


object Shared {

    val listAdv = listOf(R.mipmap.adv1, R.mipmap.adv2)

    val faceCapture = MutableLiveData<Bitmap>()

    val typeCardOcr = MutableLiveData<String>()

    val messageFail = MutableLiveData<MessageData>()

    val frameCardData = MutableLiveData<PhotoCardInfo>()

    val ocrConfirmData = MutableLiveData<IdentifyCardInfo>()

    val methodReceiveCard = MutableLiveData<MethodOfReceiving>()

    val branchList by lazy {
        readAsset("branch_list.json").parse(Array<Selectable>::class)
    }

    val provinceList by lazy {
        readAsset("province_list.json").parse(Array<Selectable>::class)
    }

    val genderList by lazy {
        readAsset("gender.json").parse(Array<Selectable>::class)
    }

}