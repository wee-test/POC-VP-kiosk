package wee.digital.sample.ui.fragment.review

import kotlinx.android.synthetic.main.review.*
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class ReviewFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.review

    override fun onViewCreated() {
        reviewTab.setUpViewPager(this, ReviewInfoFragment(), ReviewCardFragment(), ReviewFormFragment(), reviewPager)
    }

    override fun onLiveDataObserve() {}

}