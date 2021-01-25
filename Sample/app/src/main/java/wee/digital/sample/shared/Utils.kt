package wee.digital.sample.shared

import android.app.Activity
import android.graphics.Bitmap
import android.util.DisplayMetrics
import kotlin.math.roundToInt

object Utils {

    fun checkSizeBitmap(bm : Bitmap?): Boolean{
        bm ?: return false
        val w = bm.width
        val h = bm.height
        return h > (w * 0.5).roundToInt()
    }

    fun getHeightScreen(act: Activity): Int {
        val displayMetrics = DisplayMetrics()
        act.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun lengthNumberCard(type: String?): Int {
        type ?: return 9
        return when (type) {
            Configs.TYPE_NID -> 9
            Configs.TYPE_NID_12 -> 12
            Configs.TYPE_CCCD -> 12
            else -> 9
        }
    }

    fun checkPhoneInvalid(number: String): Boolean {
        if (number.isEmpty()) return true
        val first = number[0]
        return if (first.toString() == "8" && number.length == 11) {
            false
        } else !(first.toString() == "0" && number.length == 10)
    }

}