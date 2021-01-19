package wee.digital.library.widget

import android.app.Application
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import wee.digital.library.Library
import wee.digital.library.R
import wee.digital.library.extension.color

abstract class AppCustomView : ConstraintLayout {

    protected abstract fun onInitialize(context: Context, types: TypedArray)

    protected open fun styleResource(): IntArray {
        return R.styleable.CustomView
    }

    protected abstract fun layoutResource(): Int

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        onViewInit(context, attrs)
    }

    private fun onViewInit(context: Context, attrs: AttributeSet?) {
        val types = context.theme.obtainStyledAttributes(attrs, styleResource(), 0, 0)
        try {
            if (layoutResource() != 0) {
                LayoutInflater.from(context).inflate(layoutResource(), this)
            }
            onInitialize(context, types)
        } finally {
            types.recycle()
        }
    }

    val app: Application get() = Library.app

    /**
     * Text
     */
    val TypedArray.text: String?
        get() = getString(R.styleable.CustomView_android_text)

    val TypedArray.title: String?
        get() = getString(R.styleable.CustomView_android_title)

    val TypedArray.hint: String?
        get() = getString(R.styleable.CustomView_android_hint)

    val TypedArray.clickable: Boolean
        get() = getBoolean(R.styleable.CustomView_android_clickable, true)

    /**
     * Input type
     */
    val TypedArray.maxLength: Int
        get() = getInt(R.styleable.CustomView_android_maxLength, 256)

    val TypedArray.maxLines: Int
        get() = getInt(R.styleable.CustomView_android_maxLines, 1)

    val TypedArray.textAllCaps: Boolean
        get() = getBoolean(R.styleable.CustomView_android_textAllCaps, false)

    /**
     * Color
     */
    val TypedArray.tint: Int
        get() {
            return getColor(R.styleable.CustomView_android_tint, color(R.color.colorLightBlue))
        }

    val TypedArray.drawableTint: Int
        get() {
            return getColor(R.styleable.CustomView_android_drawableTint, Color.BLACK)
        }

    val TypedArray.backgroundTint: Int
        get() {
            return getColor(R.styleable.CustomView_android_backgroundTint, Color.WHITE)
        }

    val TypedArray.textColor: Int
        get() {
            return getColor(R.styleable.CustomView_android_textColor, Color.BLACK)
        }

    val TypedArray.hintColor: Int
        get() {
            return getColor(R.styleable.CustomView_android_textColor, Color.DKGRAY)
        }

    /**
     * Drawable
     */
    val TypedArray.drawableStart: Drawable?
        get() {
            return getDrawable(R.styleable.CustomView_android_drawableStart)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.drawableEnd: Drawable?
        get() {
            return getDrawable(R.styleable.CustomView_android_drawableEnd)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.drawable: Drawable?
        get() {
            return getDrawable(R.styleable.CustomView_android_drawable)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.src: Drawable?
        get() {
            return getDrawable(R.styleable.CustomView_android_src)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.srcRes: Int
        get() {
            return getResourceId(R.styleable.CustomView_android_src, 0)
        }

    val TypedArray.background: Int
        get() {
            return getResourceId(R.styleable.CustomView_android_background, 0)
        }

    /**
     * Checkable
     */
    val TypedArray.checkable: Boolean
        get() = getBoolean(R.styleable.CustomView_android_checkable, false)

    val TypedArray.checked: Boolean
        get() = getBoolean(R.styleable.CustomView_android_checked, false)

    /**
     * Padding
     */
    val TypedArray.paddingStart: Int
        get() = getDimensionPixelSize(R.styleable.CustomView_android_paddingStart, 0)

    val TypedArray.paddingEnd: Int
        get() = getDimensionPixelSize(R.styleable.CustomView_android_paddingEnd, 0)

    val TypedArray.paddingTop: Int
        get() = getDimensionPixelSize(R.styleable.CustomView_android_paddingTop, 0)

    val TypedArray.paddingBottom: Int
        get() = getDimensionPixelSize(R.styleable.CustomView_android_paddingBottom, 0)

    /**
     * Selectors
     */
    val TypedArray.enabled: Boolean
        get() = getBoolean(R.styleable.CustomView_android_enabled, true)

}


