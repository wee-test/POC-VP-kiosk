package wee.digital.sample.ui.fragment.rating

import android.view.View
import kotlinx.android.synthetic.main.rating_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.sample.R

class RatingAdapter : BaseRecyclerAdapter<RatingItem>() {

    var onItemText: (RatingItem) -> String = { it.toString() }

    var onItemSelected: (RatingItem, Int) -> Unit = { _, _ -> }

    override var onItemClick: (RatingItem, Int) -> Unit = { model, position ->
        notifyDataSetChanged()
        super.onItemClick(model, position)
        onItemSelected(model,position)
    }

    override fun layoutResource(model: RatingItem, position: Int): Int {
        return R.layout.rating_item
    }

    override fun View.onBindModel(model: RatingItem, position: Int, layout: Int) {
        model.reactIcon?.let { imageViewReact.setBackgroundResource(it) }
        textViewReact.text = model.reactContent
        if (model.isSelected) {
            textViewReact.setTextColor(context.getColor(R.color.green))
            imageViewReact.isChecked = true
        } else {
            textViewReact.setTextColor(context.getColor(R.color.black))
            imageViewReact.isChecked = false
        }
    }
}
