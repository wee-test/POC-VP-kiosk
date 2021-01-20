package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import wee.digital.library.R

abstract class AppView : FrameLayout {

    protected abstract val layoutRes: Int

    protected abstract fun onViewInit(context: Context, types: TypedArray)

    lateinit var types: TypedArray

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        onViewInit(context, attrs)
    }

    private fun onViewInit(context: Context, attrs: AttributeSet?) {
        types = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0)
        try {
            LayoutInflater.from(context).inflate(layoutRes, this)
            onViewInit(context, types)
        } finally {
            types.recycle()
        }
    }

    val tintColorRes: Int
        get() {
            val tintId =
                    types.getResourceId(R.styleable.CustomView_android_tint, android.R.color.black)
            return ContextCompat.getColor(context, tintId)
        }
    val textStyle: Int
        get() {
            return types.getResourceId(R.styleable.CustomView_android_textStyle, Typeface.NORMAL)
        }
    val drawableStartRes: Int
        get() {
            return types.getResourceId(R.styleable.CustomView_android_drawableStart, 0)
        }

    val drawableEndRes: Int
        get() {
            return types.getResourceId(R.styleable.CustomView_android_drawableEnd, 0)
        }

    val textRes: String
        get() {
            return types.getString(R.styleable.CustomView_android_text) ?: ""
        }

    val titleRes: String
        get() {
            return types.getString(R.styleable.CustomView_android_title) ?: ""
        }

    val srcRes: Int
        get() {
            return types.getResourceId(R.styleable.CustomView_android_src, 0)
        }

    val hintRes: String?
        get() {
            return types.getString(R.styleable.CustomView_android_hint)
        }

    val textColorRes: Int
        get() {
            return ContextCompat.getColor(
                    context,
                    types.getResourceId(
                            R.styleable.CustomView_android_textColor,
                            android.R.color.black
                    )
            )
        }

    val backgroundRes: Int
        get() = types.getResourceId(
                R.styleable.CustomView_android_background,
                android.R.color.white
        )

    val checkedRes: Boolean
        get() = types.getBoolean(R.styleable.CustomView_android_checked, false)

}