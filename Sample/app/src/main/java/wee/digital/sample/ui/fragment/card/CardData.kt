package wee.digital.sample.ui.fragment.card

import wee.digital.sample.R

data class CardItem(
        var name : String = "",
        var image : Int
)

val listCard1 = listOf(
        CardItem("VPBank Platium Cashback", R.mipmap.card1),
        CardItem("VPBank StepUp Mastercard", R.mipmap.card2)
)

val listCard2 = listOf(
        CardItem("VPBank Shopee Platinum", R.mipmap.card3)
)