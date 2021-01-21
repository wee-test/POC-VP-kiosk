package wee.digital.sample.ui.fragment.rating

import wee.digital.library.extension.string
import wee.digital.sample.R

class RatingItem(
        var reactIcon: Int? = R.drawable.ic_rating_love,
        var reactContent: String? = "",
        var isSelected : Boolean = false
) {
    companion object {
        val defaultList = arrayListOf(
                RatingItem(R.drawable.ic_rating_love, string(R.string.great), true),
                RatingItem(R.drawable.ic_rating_smile, string(R.string.look_good), false),
                RatingItem(R.drawable.ic_rating_neutral, string(R.string.improve), false),
                RatingItem(R.drawable.ic_rating_thinking, string(R.string.hard), false)
        )
    }
}