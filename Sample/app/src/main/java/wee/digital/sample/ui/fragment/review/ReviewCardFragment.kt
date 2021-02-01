package wee.digital.sample.ui.fragment.review

import kotlinx.android.synthetic.main.review_card.*
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class ReviewCardFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.review_card

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        Shared.cardSelected.observe {
            reviewCardImage.setImageResource(it.image)
            reviewCardNameCard.text = it.name
            reviewCardholder.text = Shared.ocrConfirmData.value?.fullName
        }
        Shared.ocrConfirmData.observe {
            it ?: return@observe
            reviewCardholder.text = it.fullName
        }
    }

}