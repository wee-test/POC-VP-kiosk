package wee.digital.camera.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import com.homesoft.encoder.FrameBuilder
import com.homesoft.encoder.MuxerConfig
import java.io.File

class RecordVideo(context: Context) {

    companion object {
        val arrayBitmap = arrayListOf<Bitmap>()
        var isRecordVideo = false
    }

    private var ct: Context = context

    private var handlerVideo: Handler? = null

    private var handlerThreadVideo: HandlerThread? = null

    private var file: File? = null

    private var config: MuxerConfig? = null

    private var frameBuilder: FrameBuilder? = null

    private var onDoneVideo = false

    fun startVideo() {
        handlerThreadVideo = HandlerThread("MyVideo")
        handlerThreadVideo?.start()
        handlerVideo = Handler(handlerThreadVideo?.looper!!)

        try {
            file = File("${ct.externalCacheDir}/${System.currentTimeMillis()}.mp4")

            val frame = (arrayBitmap.size / 7).toInt()
            config = MuxerConfig(
                    file = file!!,
                    framesPerSecond = frame.toFloat(),
                    videoWidth = 640,
                    videoHeight = 480
            )
            frameBuilder = FrameBuilder(ct, config!!, null)
            frameBuilder?.start()

            onDoneVideo = false
        } catch (e: java.lang.Exception) {
            Log.e("recordVideo", "start video fail : ${e.message}")
        }
    }

    fun createVideo(listener: MyVideoCallBack?) {
        if (arrayBitmap.isNullOrEmpty()){
            listener?.onResult("")
            return
        }
        handlerVideo?.post {
            for (i in 0 until arrayBitmap.size) {
                frameBuilder?.createFrame(arrayBitmap[i])
                if (i == arrayBitmap.size - 1) {
                    onDoneVideo(listener)
                }
            }
        }
    }

    private fun onDoneVideo(listener: MyVideoCallBack?) {
        onDoneVideo = true
        if (frameBuilder == null) {
            listener?.onResult("")
            return
        }
        Handler().postDelayed({
            try {
                frameBuilder?.releaseVideoCodec()
                frameBuilder?.releaseAudioExtractor()
                frameBuilder?.releaseMuxer()
                handlerThreadVideo?.quitSafely()
                frameBuilder = null
                handlerThreadVideo = null
                handlerVideo = null
                listener?.onResult("$file")
                file?.exists()
                arrayBitmap.clear()
            } catch (e: Exception) {
                listener?.onResult("")
            }
        }, 300)
    }

    interface MyVideoCallBack {
        fun onResult(path: String)
    }

}