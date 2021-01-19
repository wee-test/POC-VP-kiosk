package wee.digital.library.adapter

import android.view.View

class BlankAdapter(private val layoutRes: Int,
                   private val onBind: (View) -> Unit = {}
) : BaseRecyclerAdapter<Nothing>() {

    override fun blankLayoutResource(): Int {
        return layoutRes
    }

    override fun layoutResource(model: Nothing, position: Int): Int {
        return layoutRes
    }

    override fun View.onBindModel(model: Nothing, position: Int, layout: Int) {
        onBind(this)
    }

}