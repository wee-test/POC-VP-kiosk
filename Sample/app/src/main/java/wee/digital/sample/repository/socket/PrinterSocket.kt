package wee.digital.sample.repository.socket

import okhttp3.*
import okio.ByteString
import wee.digital.library.util.Logger
import java.util.concurrent.TimeUnit

class PrinterSocket {

    var url = "ws://localhost:5000/android"

    val log = Logger(this::class)

    private var ws: WebSocket? = null

    val listeners = mutableListOf<WebSocketListener>()

    fun send(data: String) {
        ws?.send(data)
    }
    fun send(data: ByteString) {
        ws?.send(data)
    }
    fun close() {
        ws?.close(1000, "Close")
    }

    fun open() {
        ws?: return
        log.d("connecting to $url")
        val client = OkHttpClient
                .Builder()
                .pingInterval(5000, TimeUnit.MILLISECONDS)
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        ws = client.newWebSocket(request, ListenerHandler())
        client.dispatcher.executorService.shutdown()
    }

    fun addListener(listener: WebSocketListener){
        listeners.add(listener)
    }

    fun removeListener(listener: WebSocketListener){
        listeners.remove(listener)
    }

    private inner class ListenerHandler : WebSocketListener(){
        override fun onOpen(webSocket: WebSocket, response: Response) {
            ws = webSocket
            listeners.forEach { it.onOpen(webSocket, response) }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listeners.forEach { it.onFailure(webSocket, t,response) }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            ws = null
            listeners.forEach { it.onClosing(webSocket, code,reason) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            listeners.forEach { it.onMessage(webSocket, text) }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            listeners.forEach { it.onMessage(webSocket, bytes) }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            listeners.forEach { it.onClosed(webSocket, code, reason) }
        }


    }

}