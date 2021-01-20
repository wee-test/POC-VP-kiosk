package wee.digital.sample.ui.fragment.home

import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home_info.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class HomeFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.home
    }

    override fun onViewCreated() {
        homeLabelName.text = getString(R.string.home_name, "Nguyen Van A")
        homeActionLogout.setOnClickListener {
            navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
        }
    }

    override fun onLiveDataObserve() {
        Shared.faceCapture.observe {
            homeImageFace.setImageBitmap(it)
        }
    }

}