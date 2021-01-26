package wee.digital.sample.ui.main

import android.os.Bundle
import android.os.SharedMemory
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.WebSocket
import wee.dev.weewebrtc.WeeCaller
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.app.lib
import wee.digital.sample.repository.socket.MySocket
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.activityVM
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private val mainVM: MainVM by lazy { activityVM(MainVM::class) }

    private var disposable : Disposable? = null

    private val weeCaller = WeeCaller(this)

    override fun layoutResource(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weeCaller.init()
        weeCaller.initUserData("909090") { userData, mess ->
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
        mainVM.loginKiosk()
    }

    override fun navController(): NavController {
        return findNavController(R.id.mainFragmentHost)
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        mainVM.dialogLiveData.observe(this::onShowDialog)
        mainVM.statusLoginKiosk.observe {
            if (it == null || it.responseCode?.code ?: -1 != 0L) {
                disposable = Observable.timer(3, TimeUnit.SECONDS).subscribe {
                    mainVM.loginKiosk()
                }
            } else {
                Configs.KIOSK_ID = it.result.kioskID
                Shared.kioskInfo.postValue(it)
            }
        }
    }

    private fun onShowDialog(directions: NavDirections?) {
        directions ?: return
        navigate(directions) {
            setVerticalAnim()
        }
    }

    private fun connectSocket() {
        Socket.action.connectWebSocketMonitor(object : MySocket.WebSocketMonitorListener {
            override fun onMessage(message: String) {
                print("")
            }

            override fun onError(webSocket: WebSocket, t: Throwable) {
                print("")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        connectSocket()
        permissionRequest(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (lib != null) return@permissionRequest
            lib = vplib.Vplib.newLib(1, "$externalCacheDir")
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        Socket.action.closeWebSocketMonitor()
    }

}
