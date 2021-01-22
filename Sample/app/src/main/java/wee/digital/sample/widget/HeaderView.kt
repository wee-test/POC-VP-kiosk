package wee.digital.sample.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_header.view.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainActivity

class HeaderView : ConstraintLayout{

    constructor(context: Context):super(context){
        initView(context, null)
    }

    constructor(context: Context, att: AttributeSet?):super(context, att){
        initView(context, att)
    }

    private fun initView(context: Context, att: AttributeSet?) {
        val style = context.theme.obtainStyledAttributes(att, wee.digital.library.R.styleable.CustomView, 0, 0)

        LayoutInflater.from(context).inflate(R.layout.view_header, this)

        val icon = style.getResourceId(R.styleable.CustomView_android_src, R.drawable.ic_placeholder)
        headerAction.setImageResource(icon)

        headerAction.setOnClickListener {
            if (icon == R.drawable.ic_cancel) {
                (context as MainActivity).navController().navigate(MainDirections.actionGlobalAdvFragment())
            }else{
                (context as MainActivity).onBackPressed()
            }
        }
    }

}