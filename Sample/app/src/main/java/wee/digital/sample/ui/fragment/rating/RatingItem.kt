package wee.digital.sample.ui.fragment.rating

import wee.digital.library.extension.string
import wee.digital.sample.R

class RatingItem(
        var reactIcon: Int? = R.drawable.ic_rating_love_select,
        var reactContent: String? = ""
) {
    companion object {
        val defaultList = listOf(
                RatingItem(R.drawable.ic_rating_love_select, string(R.string.great)),
                RatingItem(R.drawable.ic_rating_smile_select, string(R.string.look_good)),
                RatingItem(R.drawable.ic_rating_neutral_select, string(R.string.improve)),
                RatingItem(R.drawable.ic_rating_thinking_select, string(R.string.hard))
        )
    }
}