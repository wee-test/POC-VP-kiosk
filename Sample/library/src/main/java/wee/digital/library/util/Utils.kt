package wee.digital.library.util

import android.graphics.Bitmap
import kotlin.math.roundToInt

object Utils {

    fun checkSizeBitmap(bm : Bitmap?): Boolean{
        bm ?: return false
        val w = bm.width
        val h = bm.height
        return h > (w * 0.5).roundToInt()
    }

}