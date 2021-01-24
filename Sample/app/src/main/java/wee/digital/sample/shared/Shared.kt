package wee.digital.sample.shared

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import wee.digital.library.extension.parse
import wee.digital.library.extension.readAsset
import wee.digital.sample.R
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable


object Shared {

    val listAdv = listOf(R.mipmap.adv1, R.mipmap.adv2)

    val faceCapture = MutableLiveData<Bitmap>()

    val typeCardOcr = MutableLiveData<String>()

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