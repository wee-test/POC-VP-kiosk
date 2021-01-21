package wee.digital.sample.ui.fragment.home

import android.view.View
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home_info.*
import kotlinx.android.synthetic.main.home_select.*
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
        addClickListener(homeActionLogout, homeTabSelectAuto)
    }

    override fun onLiveDataObserve() {
        Shared.faceCapture.observe {
            homeImageFace.setImageBitmap(it)
        }
    }

    override fun onViewClick(v: View?) {
        when(v){
            homeActionLogout -> {
                navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
            }
            homeTabSelectAuto -> {
                navigate(MainDirections.actionGlobalDocumentFragment())
            }
        }
    }

}