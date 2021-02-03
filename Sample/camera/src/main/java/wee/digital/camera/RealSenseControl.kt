package wee.digital.camera

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.intel.realsense.librealsense.*
import wee.digital.camera.utils.RecordVideo

/**
 * Manufacture: Intel(R) RealSense(TM) Depth Camera SR305
 * Product : Intel(R) RealSense(TM) Depth Camera SR305
 * Vendor ID : 32902
 * Product ID: 2888
 */
class RealSenseControl {
    companion object{
        const val TAG = "RealSenseControl"
    }
    private val colorizer = Colorizer().apply {
        setValue(Option.COLOR_SCHEME, 0f)
    }
    private val align = Align(StreamType.COLOR)
    private var pipeline: Pipeline? = null
    private var pipelineProfile: PipelineProfile? = null

    private var colorBitmap: Bitmap? = null
    private var depthBitmap: Bitmap? = null

    private var isDestroy = false
    private var isFrameOK = false
    var isPauseCamera = false
    private var isSleep = false
    private var isProcessingFrame = false
    var isStreaming = false

    private var mFrameCount = RealSense.FRAME_MAX_COUNT

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null
    private val streamRunnable: Runnable = object : Runnable {
        override fun run() {
            var isNext = true
            var errMess = ""
            try {
                FrameReleaser().use { fr ->
                    if (isPauseCamera || isProcessingFrame || !RealSense.imagesLiveData.hasObservers()) {
                        RealSense.imagesLiveData.postValue(null)
                        isSleep = true
                    }
                    isProcessingFrame = true
                    val frames: FrameSet = pipeline!!.waitForFrames(RealSense.TIME_WAIT).releaseWith(fr)
                    if (isFrameOK) {
                        mFrameCount--
                        when {
                            mFrameCount > 0 -> {
                                val colorFrame: Frame = frames.first(StreamType.COLOR).releaseWith(fr)
                                val alignProcess = align.process(frames).releaseWith(fr)
                                val depthFrame = alignProcess.applyFilter(colorizer).releaseWith(fr)
                                        .first(StreamType.DEPTH).releaseWith(fr)
                                frameProcessing(colorFrame, depthFrame)
                            }
                            mFrameCount < RealSense.FRAME_MAX_SLEEP -> {
                                mFrameCount = RealSense.FRAME_MAX_COUNT
                            }
                        }
                    }
                    isFrameOK = true
                    isSleep = false
                }
            } catch (e: Exception) {
                errMess = e.message.toString()
                isNext = false
            } finally {
                isProcessingFrame = false
            }

            if (isNext) {
                if(mHandlerThread?.isAlive==true) {
                    mHandler?.post(this)
                }
            } else {
                Log.d(TAG,"Err: $errMess")
                isFrameOK = false
                isStreaming = false
                hardwareReset()
            }
        }
    }

    init {
        mHandlerThread = HandlerThread("streaming").also {
            it.start()
            mHandler = Handler(it.looper)
        }
    }

    fun onCreate(rsContext: RsContext?) {
        if(isDestroy)
        if (isStreaming) return
        try {
            val config = Config().apply {
                enableStream(
                        StreamType.COLOR, 0,
                        RealSense.COLOR_WIDTH, RealSense.COLOR_HEIGHT,
                        StreamFormat.RGB8, RealSense.FRAME_RATE
                )
                enableStream(
                        StreamType.DEPTH, 0,
                        RealSense.DEPTH_WIDTH, RealSense.DEPTH_HEIGHT,
                        StreamFormat.Z16, RealSense.FRAME_RATE
                )
            }

            pipeline = Pipeline()
            pipelineProfile = pipeline?.start(config)?.apply {
                isStreaming = true
                mHandler?.post(streamRunnable)
                Log.d(TAG,"onCreate")
            }
        } catch (t: Throwable) {
            isStreaming = false
        }
    }

    fun onStop() {
        if(isDestroy) return
        try {
            isStreaming = false
            isDestroy = true
            mHandlerThread?.quit()
            mHandlerThread = null
            RealSense.imagesLiveData.postValue(null)
            pipeline?.stop()
            pipelineProfile?.close()
            Log.d(TAG,"onStop")
        } catch (t: Throwable) {
            Log.e("RealSense","onPause: ${t.message}")
        }
    }

    private fun frameProcessing(colorFrame: Frame, depthFrame: Frame) {
        try {
            colorBitmap = colorFrame.rgbToBitmapOpenCV()
            depthBitmap = depthFrame.rgbToBitmapOpenCV()
            if (colorBitmap != null && depthBitmap != null) {
                Log.e("realsenseFrame", "frame")
                if(RecordVideo.isRecordVideo) RecordVideo.arrayBitmap.add(colorBitmap!!)
                RealSense.imagesLiveData.postValue(Pair(colorBitmap!!, depthBitmap!!))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            isProcessingFrame = false
        }
    }

    fun hasFace() {
        mFrameCount = RealSense.FRAME_MAX_COUNT
    }

    private fun hardwareReset() {
        try {
            pipelineProfile?.device?.hardwareReset()
            Log.d(TAG,"hardwareReset")
        } catch (ignore: RuntimeException) {
        }
    }


}