package wee.digital.sample.ui.fragment.member

import android.view.View
import kotlinx.android.synthetic.main.fail.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class FailFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.fail

    override fun onViewCreated() {
        addClickListener(failActionAgain)
    }

    override fun onLiveDataObserve() {
        Shared.messageFail.observe {
            failTitle.text = it.title
            failContent.text = it.message
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            failActionAgain -> {
                navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
            }
        }
    }

}