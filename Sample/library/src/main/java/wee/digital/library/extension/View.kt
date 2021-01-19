package wee.digital.library.extension

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import wee.digital.library.Library
import kotlin.math.roundToInt


private val app: Application get() = Library.app

fun Float.dpToPx(): Float {
    val resources = app.resources
    val scale = resources.displayMetrics.density
    return (this * scale + 0.5f)
}

fun Float.spToPx(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics).roundToInt()
}

fun Float.dpToSp(): Int {
    return (this.dpToPx() / this.spToPx().toFloat()).roundToInt()
}

fun Float.pxToDp(): Float {
    return this / (app.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.dipToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, app.resources.displayMetrics)
}

fun Int.isDarkColor(): Boolean {
    val darkness = 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= 0.5
}

@ColorInt
fun Int.darker(factor: Float): Int {
    val a = Color.alpha(this)
    val r = (Color.red(this) * factor).toDouble().roundToInt()
    val g = (Color.green(this) * factor).toDouble().roundToInt()
    val b = (Color.blue(this) * factor).toDouble().roundToInt()
    return Color.argb(a,
            r.coerceAtMost(255),
            g.coerceAtMost(255),
            b.coerceAtMost(255))
}

fun View.getSize(block: (Int /*width*/, Int/*height*/) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            block(width, height)
        }
    })
}

fun View.show() {
    post {
        if (visibility != View.VISIBLE) visibility = View.VISIBLE
    }
}

fun View.isShow(show: Boolean?) {
    visibility = if (show == true) View.VISIBLE
    else View.INVISIBLE
}

fun View.hide() {
    post {
        visibility = View.INVISIBLE
    }
}

fun View.isHide(hide: Boolean?) {
    visibility = if (hide == true) View.INVISIBLE
    else View.VISIBLE
}

fun View.gone() {
    post { visibility = View.GONE }
}

fun View.isGone(gone: Boolean?) {
    visibility = if (gone == true) View.GONE
    else View.VISIBLE
}

fun View.getBitmap(w: Int = width, h: Int = height, block: (Bitmap?) -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            this@getBitmap.removeOnLayoutChangeListener(this)
            v.post {
                val bitmap = getBitmap(w, h)
                block(bitmap)
            }
        }
    })
}

fun View.getBitmap(w: Int = width, h: Int = height): Bitmap? {
    return try {
        if (w > 0 && h > 0) {
            this.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
        }
        this.layout(0, 0, this.measuredWidth, this.measuredHeight)
        val bitmap = Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.background?.draw(canvas)
        this.draw(canvas)
        bitmap
    } catch (ignore: Exception) {
        null
    }
}

val View.activity: Activity? get() = context as? Activity

val View.fragmentActivity: FragmentActivity? get() = context as? FragmentActivity

val View?.backgroundColor: Int
    get() {
        this ?: return Color.WHITE
        this.background ?: return Color.WHITE
        var color = Color.TRANSPARENT
        val background: Drawable = this.background
        if (background is ColorDrawable) color = background.color
        return color
    }

fun View?.post(delayed: Long, runnable: Runnable) {
    this?.postDelayed(runnable, 800)
}

fun show(vararg views: View?) {
    for (v in views) v?.show()
}

fun hide(vararg views: View?) {
    for (v in views) v?.hide()
}

fun gone(vararg views: View?) {
    for (v in views) v?.gone()
}

fun Context.view(@LayoutRes layoutRes: Int): View {
    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater.inflate(layoutRes, null)
}

fun View.backgroundTint(@ColorInt color: Int) {
    post {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            background?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}

fun View.backgroundTintRes(@ColorRes colorRes: Int) {
    backgroundTint(color(colorRes))
}

