package wee.digital.sample.ui.fragment.dialog.selectable

import android.view.View
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.selectable_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.bold
import wee.digital.library.extension.isGone
import wee.digital.library.extension.regular
import wee.digital.sample.R


open class SelectableAdapter<T : Selectable> : BaseRecyclerAdapter<T>() {

    @StringRes
    var title: Int = R.string.app_name

    var onItemSelected: (T) -> Unit = { }

    var onDismiss: () -> Unit = {}

    open var selectedItem: T? = null

    override var onItemClick: (T, Int) -> Unit = { model, position ->
        selectedItem = model
        notifyDataSetChanged()
        super.onItemClick(model, position)
        onItemSelected(model)
    }

    override fun layoutResource(model: T, position: Int): Int {
        return R.layout.selectable_item
    }

    override fun View.onBindModel(model: T, position: Int, layout: Int) {
        selectableImageView.isGone(model.icon == 0)
        selectableTextViewItem.setBackgroundResource(if (position != lastIndex) R.drawable.drw_underline else R.color.colorWhite)
        selectableImageView.setImageResource(model.icon ?: 0)
        selectableTextViewItem.text = model.NAME
        /*if (model != selectedItem) {
            selectableTextViewItem.regular()
        } else {
            selectableTextViewItem.bold()
        }*/
    }
}