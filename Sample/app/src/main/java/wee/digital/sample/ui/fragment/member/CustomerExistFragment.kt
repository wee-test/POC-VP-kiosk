package wee.digital.sample.ui.fragment.member

import android.view.View
import kotlinx.android.synthetic.main.customer_exist.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class CustomerExistFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.customer_exist

    override fun onViewCreated() {
        addClickListener(customerAction)
    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when (v) {
            customerAction -> navigate(MainDirections.actionGlobalAdvFragment())
        }
    }

}