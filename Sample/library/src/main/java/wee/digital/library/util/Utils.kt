package wee.digital.library.util

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

    fun getHeightScreen(act: Activity) : Int{
        val displayMetrics = DisplayMetrics()
        act.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

}