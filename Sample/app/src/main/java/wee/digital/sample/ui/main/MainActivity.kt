package wee.digital.sample.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import wee.dev.weewebrtc.WeeCaller
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.activityVM

class MainActivity : BaseActivity() {

    val mainVM: MainVM by lazy { activityVM(MainVM::class) }

    val weeCaller = WeeCaller(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weeCaller.init()
        weeCaller.initUserData("909090"){userData, mess ->
            toast("${userData?.Name} - ${userData?.ReceiverID} - $mess")
            /*weeCaller.sendCall("20351104",null,mainVideoCallView,object : WeeWebRTC.WebRTCListener{
                override fun onClosed() {
                    toast("onClosed")
                }

                override fun onConnected() {
                    toast("onConnected")
                }

                override fun onError(mess: String) {
                    toast("onError: $mess")
                }

                override fun onMessage(mess: String) {
                    toast("onMessage: $mess")
                }

                override fun onReceiverCall(id: String) {
                    toast("onReceiverCall: $id")
                }

                override fun onSendCall(id: String) {
                    toast("onSendCall: $id")
                }

                override fun onStart() {
                    toast("onStart")
                }

            })*/
        }
    }

    override fun layoutResource(): Int {
        return R.layout.activity_main
    }


    override fun navController(): NavController {
        return findNavController(R.id.mainFragmentHost)
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        mainVM.dialogLiveData.observe(this::onShowDialog)
    }

    private fun onShowDialog(directions: NavDirections?) {
        directions ?: return
        navigate(directions) {
            setVerticalAnim()
        }
    }

}
