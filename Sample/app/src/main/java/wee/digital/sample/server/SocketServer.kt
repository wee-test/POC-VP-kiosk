package wee.digital.sample.server

import android.util.Log
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.nio.ByteBuffer


class SocketServer(address: InetSocketAddress?) : WebSocketServer(address) {
    private var event: Listener? = null
    //private val executors = Executors.newSingleThreadExecutor()

    fun setListener(event: Listener){
        this.event = event
    }
    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        /*conn.send("Welcome to the server!") //This method sends a message to the new client
        broadcast("new connection: " + handshake.resourceDescriptor) //This method sends a message to all clients connected
        println(conn.remoteSocketAddress.address.hostAddress + " entered the websocket!")
        Log.e(TAG,"onOpen")*/
        event?.onOpen(conn,handshake)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        /*broadcast("$conn has left the room!")
        println("$conn has left the room!")
        Log.e(TAG,"onClose")*/
        event?.onClose(conn,code,reason,remote)
    }

    override fun onMessage(conn: WebSocket, message: String) {
        /*broadcast("$conn: $message")
        println("$conn: $message")
        Log.e(TAG,"onMessage: $message")*/
        event?.onMessage(conn,message)
    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
       /* broadcast(message.array())
        println("$conn: $message")*/
        Log.e(TAG,"onMessage")
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        /*ex.printStackTrace()
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
        Log.e(TAG,"onError")*/
        event?.onError(conn,ex)
    }

    override fun onStart() {
        println("Server started!")
        connectionLostTimeout = 0
        connectionLostTimeout = 100
        Log.e(TAG,"Server started!")
        event?.onStarted()
    }

    fun sendStreamData(data: String){
        broadcast(data)
    }

    companion object {
        const val TAG = "SocketServer"
        var LIST_CONN = arrayListOf<WebSocket>()
    }

    interface Listener{
        fun onOpen(conn: WebSocket, handshake: ClientHandshake)
        fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean)
        fun onMessage(conn: WebSocket, message: String)
        fun onError(conn: WebSocket?, ex: Exception)
        fun onStarted()
    }
}