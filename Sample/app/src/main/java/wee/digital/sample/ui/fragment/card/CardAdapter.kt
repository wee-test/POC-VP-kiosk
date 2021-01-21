package wee.digital.sample.ui.fragment.card

import android.view.View
import kotlinx.android.synthetic.main.card_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.sample.R

class CardAdapter : BaseRecyclerAdapter<CardItem>(){

    override fun layoutResource(model: CardItem, position: Int): Int = R.layout.card_item

    override fun View.onBindModel(model: CardItem, position: Int, layout: Int) {
        cardImageView.setImageResource(model.image)
        cardTextViewTitle.text = model.name
    }

}