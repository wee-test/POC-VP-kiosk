package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.children
import kotlinx.android.synthetic.main.widget_toggle_radio_btn.view.*
import wee.digital.library.extension.isGone
import wee.digital.library.widget.AppCustomView
import wee.digital.sample.R

class ToggleRadioButton : AppCustomView {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun layoutResource(): Int {
        return R.layout.widget_toggle_radio_btn
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        text = types.text
        src = types.srcRes
        isChecked = types.checked
    }

    override fun onViewClick(v: View?) {
        isChecked = !isChecked
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        when {
            child == null || params == null -> return
            child?.id == R.id.radioLayout -> {
                super.addView(child, index, params)
                addViewClickListener(child.radioImageViewIcon, child.radioTextViewLabel, child.radioImageViewCheck)
            }
            else -> {
                radioLayout.radioExpandableLayout.addView(child, index, params)
            }
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        radioExpandableLayout.setPadding(left, top, right, bottom)
    }
    @DrawableRes
    var src: Int = 0
        set(value) {
            radioImageViewIcon.isGone(value <= 0)
            radioImageViewIcon.setImageResource(value)
            field = value
        }

    var text: String? = null
        set(value) {
            radioTextViewLabel.text = value
            field = value
        }

    var isChecked: Boolean = false
        set(value) {
            if (value) {
                radioImageViewCheck.setImageResource(R.drawable.ic_toggle_radio_checked)
                radioExpandableLayout.expand()
                (parent as? ViewGroup)?.children?.forEach {
                    if (it != this && it is ToggleRadioButton) {
                        it.isChecked = false
                    }
                }
            } else {
                radioImageViewCheck.setImageResource(R.drawable.ic_toggle_radio_uncheck)
                radioExpandableLayout.collapse()
            }
            field = value
        }

}