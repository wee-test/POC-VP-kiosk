package wee.digital.sample.repository.socket

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import wee.digital.sample.repository.model.SocketReq

class Socket {

    companion object {
        val action: Socket by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Socket() }
    }

    var webSocketControlMonitorV2: MySocket? = null

    fun connectWebSocketMonitor(kioskId: String, tellersId: String, webSocketMonitorListener: MySocket.WebSocketMonitorListener) {
        if (webSocketControlMonitorV2 == null) {
            Log.d("connectWebSocketMonitor", "Open Connect WebSocketMonitor")
            webSocketControlMonitorV2 = MySocket()
            webSocketControlMonitorV2?.addWebSocketListener(webSocketMonitorListener)
            webSocketControlMonitorV2?.openConnect(kioskId, tellersId)
            return
        }

        closeWebSocketMonitor(object : MySocket.WebSocketMonitorCloseListener {

            override fun onClosed() {
                Log.d("connectWebSocketMonitor", "closeWebSocketMonitor")
                webSocketControlMonitorV2 = null
                connectWebSocketMonitor(kioskId, tellersId, webSocketMonitorListener)
            }

        })
    }

    fun closeWebSocketMonitor(webSocketMonitorCloseListener: MySocket.WebSocketMonitorCloseListener? = null) {
        webSocketControlMonitorV2?.closeConnect(webSocketMonitorCloseListener)
    }

    @SuppressLint("CheckResult")
    fun sendData(data : SocketReq?){
        data ?: return
        val str = Gson().toJson(data)
        webSocketControlMonitorV2?.sendData(str)
    }

}