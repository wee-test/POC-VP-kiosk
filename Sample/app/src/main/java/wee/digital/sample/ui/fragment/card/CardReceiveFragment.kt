package wee.digital.sample.ui.fragment.card

import android.view.View
import kotlinx.android.synthetic.main.card_receive_method.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class CardReceiveFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.card_receive_method
    }

    override fun onViewCreated() {
        addClickListener(cardReceiveActionNext)
    }

    override fun onViewClick(v: View?) {
        when(v){
            cardReceiveActionNext -> {
                navigate(MainDirections.actionGlobalReviewFragment())
            }
        }
    }

    override fun onLiveDataObserve() {
    }
}