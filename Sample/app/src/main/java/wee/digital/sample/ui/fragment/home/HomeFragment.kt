package wee.digital.sample.ui.fragment.home

import android.view.View
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.home_info.*
import kotlinx.android.synthetic.main.home_select.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Configs
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
    }

    override fun onLiveDataObserve() {
        Shared.faceCapture.observe {
            homeImageFace.setImageBitmap(it)
        }
        Shared.customerInfoExist.observe {
            if (it.customerInfo.identityCardInfo.fullName.isNullOrEmpty()) {
                Voice.ins?.request(VoiceData.HI_UNKNOWN)
            } else {
                homeLabelName.text = "Xin chào, Quý khách\n${it.customerInfo.identityCardInfo.fullName}"
                Voice.ins?.request("Xin chào, ${it.customerInfo.identityCardInfo.fullName}")
            }
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
                Configs.isMute = true
                navigate(MainDirections.actionGlobalCallFragment())
            }
        }
    }

}