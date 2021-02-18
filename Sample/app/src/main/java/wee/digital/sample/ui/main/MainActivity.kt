package wee.digital.sample.ui.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_card1_front.*
import kotlinx.android.synthetic.main.main_card2_front.*
import okhttp3.Response
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import wee.dev.weewebrtc.WeeCaller
import wee.dev.weewebrtc.`interface`.CallListener
import wee.dev.weewebrtc.repository.model.CallLog
import wee.dev.weewebrtc.repository.model.RecordData
import wee.digital.camera.toBytes
import wee.digital.library.extension.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.MessageData
import wee.digital.sample.repository.socket.MySocket
import wee.digital.sample.repository.socket.PrinterSocket
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.server.SocketServer
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.activityVM
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MainActivity : BaseActivity(), SocketServer.Listener {

    private val mainVM: MainVM by lazy { activityVM(MainVM::class) }

    private var tellerId: String = ""

    private var disposable: Disposable? = null

    private var isStartedServer = false

    private var socketServer: SocketServer? = null

    private val WS_PORT = 8888

    val printerSocket = PrinterSocket()

    companion object{
       var weeCaller: WeeCaller? = null
    }

    override fun layoutResource(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weeCaller = WeeCaller(this)
        weeCaller?.init()
        printerSocket.addListener(MyWebSocketListenr())
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
        Shared.socketStatusConnect.observe {
            if (it == null) {
                Socket.action.closeWebSocketMonitor()
            } else {
                val tellersId = it.listTellersIDString?.toArray()?.get(0)?.asString ?: ""
                Log.e("callVideoData", "${Configs.KIOSK_ID} - $tellersId")
                if (tellersId == "null") {
                    Shared.messageFail.postValue(
                            MessageData("Không thể thực hiện cuộc gọi vào lúc này",
                                    "bạn vui lòng thử lại")
                    )
                    navigate(MainDirections.actionGlobalFailFragment())
                    return@observe
                }
                connectSocket(Configs.KIOSK_ID, tellersId)
                Shared.callVideo.postValue(tellersId)
            }
        }
        Shared.callVideo.observe {
            if (it.isNullOrEmpty()) {
                weeCaller?.callHangUp()
            } else {
                callVideo(it)
            }
        }
        Shared.wsMessage.observe { mess ->
            socketServer?.sendStreamData(mess)
        }

    }

    private fun callVideo(tellersId: String) {
        weeCaller?.initUserData(Configs.KIOSK_ID) { userData, mess ->
            weeCaller?.sendCall(tellersId, mainVideoCallView, remoteVideoCallView, false, object : CallListener {
                override fun onCallLog(callLog: CallLog) {
                    toast(callLog.StatusCall)
                    Shared.dataCallLog = callLog
                }

                override fun onClosed() {
                    runOnUiThread { remoteVideoCallView.gone() }
                }

                override fun onConnected() {
                    runOnUiThread {
                        remoteVideoCallView.show()
                        navigate(MainDirections.actionGlobalDocumentFragment())
                    }
                }

                override fun onError(mess: String) {
                    runOnUiThread { remoteVideoCallView.gone() }
                }

                override fun onMessage(mess: String) {
                    toast("onMessage: $mess")
                }

                override fun onReceiverCall(id: String) {
                    toast("onReceiverCall: $id")
                }

                override fun onRecordedFile(recordData: RecordData) {
                    Log.e("onRecordedFile", "recordVideo")
                    mainVM.recordVideo(recordData)
                }

                override fun onSendCall(id: String) {
                    toast("onSendCall: $id")
                }

                override fun onStart() {
                    toast("onStart")
                }

            })
        }
    }

    private fun onShowDialog(directions: NavDirections?) {
        directions ?: return
        navigate(directions) {
            setVerticalAnim()
        }
    }

    private fun connectSocket(kioskId: String, tellersId: String) {
        Socket.action.connectWebSocketMonitor(kioskId, tellersId, object : MySocket.WebSocketMonitorListener {
            override fun onMessage(message: String) {

            }

            override fun onError(webSocket: okhttp3.WebSocket, t: Throwable) {
                post(400) { connectSocket(Configs.KIOSK_ID, tellerId) }
            }
        })
    }

    fun bindCardColorFront(number: String, name: String, exDate: String) {
        card1FrontNumberCard.text = number
        card1FrontLabelName.text = name
        card1FrontLabelExDate.text = exDate
        val bitmap = card1FrontRootFront.getBitmap()
        val bytes = bitmap.toBytes()
        val byteString = ByteBuffer.wrap(bytes, 0, bytes.size).toByteString()
        printerSocket.send(byteString)
    }

    fun bindCardBlackWhiteFront(number: String, name: String, exDate: String) {
        card2FrontNumberCard.text = number
        card2FrontLabelName.text = name
        card2FrontLabelExDate.text = exDate


        //compress
        //val stream = ByteArrayOutputStream()
        //bitmap?.compress(Bitmap.CompressFormat.JPEG,80,stream)
        val bitmap = card2FrontRootFront.getBitmap()
        val bytes = bitmap.toBytes()
        val byteString = ByteBuffer.wrap(bytes, 0, bytes.size).toByteString()
        printerSocket.send(byteString)
    }

    override fun onResume() {
        super.onResume()
        permissionRequest(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (lib != null) return@permissionRequest
            lib = vplib.Vplib.newLib(1, "$externalCacheDir")
            mainVM.loginKiosk()
        }
        startWebSocket()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        printerSocket.close()
        socketServer?.stop()
    }

    private inner class MyWebSocketListenr : WebSocketListener() {
        override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
            mainThread {
                mainTextViewPrinter.text = "open"
            }
        }

        override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
            mainThread {
                mainTextViewPrinter.text = "close"
            }
        }
    }

    private fun startWebSocket() {
        getIpAddressStartServer { ip, err ->
            runOnUiThread {
                if (ip.isNotEmpty()) {
                    startWebSocketServer(ip)
                } else {
                    isStartedServer = false
                    Handler(mainLooper).postDelayed({
                        startWebSocket()
                    }, 5000)
                }

            }

        }
    }

    private fun getIpAddressStartServer(block: (ipAddress: String, err: String) -> Unit) {
        thread(start = true) {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                val listIP = arrayListOf<String>()
                while (en.hasMoreElements()) {
                    val intFace = en.nextElement()
                    val enumIpAdd = intFace.inetAddresses
                    while (enumIpAdd.hasMoreElements()) {
                        val iNetAddress = enumIpAdd.nextElement()
                        if (!iNetAddress.isLoopbackAddress && iNetAddress is Inet4Address) {
                            val ipAddress = iNetAddress.getHostAddress().toString()
                            Log.e("IPAddress", "$ipAddress - $iNetAddress - $enumIpAdd - $$intFace")
                            listIP.add(ipAddress)
                        }
                    }
                }
                val ip = listIP.firstOrNull() ?: ""
                block(ip, "")
                return@thread
            } catch (ex: SocketException) {
                Log.e("Socket exception", ex.toString())
                block("", "${ex.message}")
                return@thread
            }
        }

    }

    private fun startWebSocketServer(ipAddress: String) {
        try {
            if (isStartedServer) return
            Log.e("WebSocketServer", "startWebSocketServer $ipAddress:$WS_PORT")
            val iNetSocket = if (ipAddress.isNotEmpty()) {
                InetSocketAddress(ipAddress, WS_PORT)
            } else {
                InetSocketAddress(WS_PORT)
            }
            if (socketServer != null) {
                socketServer?.stop().apply {
                    socketServer = null
                }
            }
            socketServer = SocketServer(iNetSocket)
            socketServer?.isReuseAddr = true
            socketServer?.start()
            socketServer?.setListener(this)
            isStartedServer = true
            toast("Server: $ipAddress:$WS_PORT")
            Shared.wsServer.postValue("$ipAddress:$WS_PORT")
        } catch (e: java.lang.Exception) {
            Log.e("WebSocketServer", "${e.message}")
            isStartedServer = false
            toast("Error: ${e.printStackTrace()}")
            Shared.wsServer.postValue(null)
        }

    }

    private fun stopWebSocketServer() {
        socketServer?.stop()
        socketServer = null
        isStartedServer = false
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {

    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {

    }

    override fun onMessage(conn: WebSocket, message: String) {

    }

    override fun onError(conn: WebSocket?, ex: Exception) {

    }

    override fun onStarted() {

    }

}