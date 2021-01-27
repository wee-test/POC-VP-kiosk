package wee.digital.sample.repository.socket

import android.util.Log
import okhttp3.*
import okio.ByteString
import wee.digital.sample.shared.Configs
import java.util.concurrent.TimeUnit

class MySocket : WebSocketListener(){

    companion object {
        const val TAG = "WebSocketMonitorV2"
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private var mClient = OkHttpClient()
    private var mWS: WebSocket? = null
    private var mRequest: Request? = null
    private var mURLConnecting = ""
    private var mURLConnected = ""

    //---
    private var isOpen = false
    private var mTimeIn = 0L

    //---
    private var mWebSocketMonitorListener: WebSocketMonitorListener? = null
    private var mWebSocketMonitorCloseListener: WebSocketMonitorCloseListener? = null
    //---

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(TAG, "onOpen - $response")
        mURLConnected = mURLConnecting
        mWebSocketMonitorListener?.onConnected(webSocket, response)
        isOpen = true
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(TAG, "onFailure - $t - $response")
        isOpen = false
        mWebSocketMonitorListener?.onError(webSocket, t)
        mWebSocketMonitorCloseListener?.onClosed()
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        isOpen = false
        Log.d(TAG, "onClosing - $code - $reason")
        if (code == 3012) {
            mWebSocketMonitorListener?.onClosing(reason)
            mWebSocketMonitorCloseListener?.onClosed()
        }
        if (code == 1000) {
            mWebSocketMonitorListener?.unKnownError(reason)
            mWebSocketMonitorCloseListener?.onClosed()
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        mWebSocketMonitorListener?.onMessage(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d(TAG, "onMessage bytes - $bytes")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d(TAG, "onClosed - [${System.currentTimeMillis() - mTimeIn}] - $code - $reason")
        isOpen = false
        if (code != 3012 && code != 1000) {
            mWebSocketMonitorListener?.onDisconnected("onClosed - [${System.currentTimeMillis() - mTimeIn}] - $code - $reason")
            mWebSocketMonitorCloseListener?.onClosed()
        }
    }

    fun sendData(data: String) {
        if (isOpen) {
            mWS!!.send(data)
            Log.d(TAG, "Sent - $data")
        } else {
            Log.d(TAG, "WebSocket not Open")
        }
    }

    fun closeConnect(webSocketMonitorCloseListener: WebSocketMonitorCloseListener?) {
        mWebSocketMonitorCloseListener = webSocketMonitorCloseListener
        if (isOpen) {
            mTimeIn = System.currentTimeMillis()
            mWS!!.close(NORMAL_CLOSURE_STATUS, "Usb: Say Goodbye !")
        } else {
            mWebSocketMonitorCloseListener?.onClosed()
        }
    }

    fun openConnect(kioskId :String, tellersId :String) {
        if (isOpen) return
        val url = "${Configs.SOCKET_URL}/kiosk/form-stream/connect?kioskId=$kioskId&tellersId=$tellersId"
        mTimeIn = System.currentTimeMillis()
        Log.d("Open Connect", "Connecting")
        mURLConnecting = url
        mClient = OkHttpClient
                .Builder()
                .pingInterval(5000, TimeUnit.MILLISECONDS)
                .build()
        Log.d(TAG, "Connecting to $mURLConnecting")
        mRequest = Request.Builder()
                .url(mURLConnecting)
                .build()
        mWS = mClient.newWebSocket(mRequest!!, this)
        mClient.dispatcher.executorService.shutdown()
    }

    fun addWebSocketListener(webSocketMonitorListener: WebSocketMonitorListener) {
        mWebSocketMonitorListener = webSocketMonitorListener
    }

    interface WebSocketMonitorListener {
        fun onConnected(webSocket: WebSocket, response: Response) {}
        fun onDisconnected(message: String) {}
        fun onClosing(message: String) {}
        fun unKnownError(message: String) {}
        fun onMessage(message: String)
        fun onError(webSocket: WebSocket, t: Throwable)
    }

    interface WebSocketMonitorCloseListener {
        fun onClosed()
    }


}