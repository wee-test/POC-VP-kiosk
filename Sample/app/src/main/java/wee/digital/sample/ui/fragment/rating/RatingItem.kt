package wee.digital.sample.ui.fragment.rating

import wee.digital.library.extension.string
import wee.digital.sample.R

class RatingItem(
        var reactIcon: Int? = R.drawable.ic_rating_love,
        var reactContent: String? = string(R.string.great),
        var isSelected : Boolean = true,
        var type : Int = 0
) {
    companion object {
        val defaultList = arrayListOf(
                RatingItem(R.drawable.ic_rating_love, string(R.string.great), true, 1),
                RatingItem(R.drawable.ic_rating_smile, string(R.string.look_good), false, 2),
                RatingItem(R.drawable.ic_rating_neutral, string(R.string.improve), false,3),
                RatingItem(R.drawable.ic_rating_thinking, string(R.string.hard), false, 4)
        )
    }
}