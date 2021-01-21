package wee.digital.sample.ui.fragment.rating

import android.view.View
import kotlinx.android.synthetic.main.rating_item.view.*
import wee.digital.library.extension.bold
import wee.digital.library.extension.drawable
import wee.digital.library.extension.regular
import wee.digital.sample.R
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableAdapter

/*
class RatingAdapter : SelectableAdapter<RatingItem>() {

    override fun layoutResource(model: RatingItem, position: Int): Int {
        return R.layout.rating_item
    }

    override fun View.onBindModel(model: RatingItem) {
        model.reactIcon?.let { imageViewReact.setImageResource(it) }
        textViewReact.text = model.reactContent
    }

    override fun View.onBindModelSelected(model: RatingItem) {
        backgroundReact.background = drawable(R.drawable.bg_react)
        textViewReact.bold()
    }

    override fun View.onBindModelUnselected(model: RatingItem) {
        backgroundReact.background = null
        textViewReact.regular()
    }

}*/
