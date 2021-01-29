package wee.digital.sample.ui.fragment.home

import android.view.View
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home_info.*
import kotlinx.android.synthetic.main.home_select.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class HomeFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.home
    }

    override fun onViewCreated() {
        addClickListener(homeActionLogout, homeTabSelectAuto, homeTabSelectSupport)
        Voice.ins?.request(VoiceData.HI_UNKNOWN)
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
            homeTabSelectSupport -> {
                navigate(MainDirections.actionGlobalCallFragment())
            }
        }
    }

}