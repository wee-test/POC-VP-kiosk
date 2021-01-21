package wee.digital.sample.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import wee.digital.sample.R

class HeaderView : ConstraintLayout{

    constructor(context: Context):super(context){
        initView(context, null)
    }

    constructor(context: Context, att: AttributeSet?):super(context, att){
        initView(context, att)
    }

    private fun initView(context: Context, att: AttributeSet?){
        LayoutInflater.from(context).inflate(R.layout.view_header, this)
    }

}