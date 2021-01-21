package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.widget_icon_textview.view.*
import wee.digital.library.widget.AppCustomView
import wee.digital.sample.R

class IconTextView: AppCustomView {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    @DrawableRes
    var src: Int = 0
        set(value) {
            textViewIcon.setImageResource(value)
        }

    var title: String? = null
        set(value) {
            textViewTitle.text = value
        }

    var color: Int= 0
        set(value) {
          textViewTitle.setTextColor(value)
        }

    var tint = 0
        set(value) {
            textViewIcon.apply { setColorFilter(value) }
        }

    override fun onInitialize(context: Context, types: TypedArray) {
        title = types.title
        src = types.srcRes
        color= types.textColor
        tint = types.tint
        textViewTitle.setTypeface(textViewTitle.typeface,types.textStyle)
    }

    override fun layoutResource(): Int {
        return R.layout.widget_icon_textview
    }
}