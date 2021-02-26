package wee.digital.sample.ui.fragment.card

import wee.digital.sample.R

data class CardItem(
        var type :String = "",
        var name : String = "",
        var image : Int
)

val listCard1 = listOf(
        CardItem("PLATINUM_CASHBACK","Thẻ Diamond World Lady", R.mipmap.card1),
        CardItem("AUTOLINK","Thẻ VPBank StepUp", R.mipmap.card2)
)

val listCard2 = listOf(
        CardItem("SHOPEE_PLATINUM","Thẻ VPBank Shopee Platinum", R.mipmap.card3)
)