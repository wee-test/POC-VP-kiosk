package wee.digital.sample.shared

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import wee.digital.sample.R


object Shared {

    val listAdv = listOf(R.mipmap.adv1, R.mipmap.adv2)

    val faceCapture = MutableLiveData<Bitmap>()

}