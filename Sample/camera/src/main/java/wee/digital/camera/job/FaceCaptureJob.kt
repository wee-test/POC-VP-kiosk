package wee.digital.camera.job

import android.graphics.Bitmap
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import wee.digital.camera.RealSense
import wee.digital.camera.detector.FaceDetector
import wee.digital.camera.uiThread
import java.util.concurrent.atomic.AtomicInteger


class FaceCaptureJob(private val listener: Listener) :
    FaceDetector.DataListener,
    FaceDetector.OptionListener,
    FaceDetector.StatusListener {

    companion object {
        const val MIN_SIZE = 100
        const val MAX_SIZE = 580
    }

    private var hasDetect: Boolean = false

    private val invalidFaceCount = AtomicInteger()

    private val captureTimer = CaptureTimer()

    private val detector: FaceDetector = FaceDetector().also {
        it.dataListener = this
        it.optionListener = this
        it.statusListener = this
    }

    private val imagesObserver = Observer<Pair<Bitmap, Bitmap>?> {
        it?.apply {
            if (hasDetect) detector.detectFace(first, second)
        }
    }

    fun observe(lifecycleOwner: LifecycleOwner) {
        startDetect()
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun destroy() {
                pauseDetect()
            }
        })
    }

    fun startDetect() {
        invalidFaceCount.set(0)
        if (timeout > 0) timeoutHandler.removeCallbacks(timeoutRunnable)
        RealSense.imagesLiveData.observeForever(imagesObserver)
        hasDetect = true
    }

    fun pauseDetect() {
        hasDetect = false
        RealSense.imagesLiveData.removeObserver(imagesObserver)
        detector.destroy()
    }

    /**
     * [FaceDetector.OptionListener] implement
     */
    override fun onFaceScore(score: Float): Boolean {
        return score > 0.9
    }

    override fun onFaceRect(left: Int, top: Int, width: Int, height: Int): Boolean {
        return when (width) {
            in 0 until MIN_SIZE -> {
                onPortraitInvalid("Mời Quý khách đưa gương mặt lại gần hơn")
                false
            }
            in MIN_SIZE..MAX_SIZE -> {
                true
            }
            else -> {
                onPortraitInvalid("Mời Quý khách đưa gương mặt lại gần hơn")
                false
            }
        }
    }


    /**
     * [FaceDetector.StatusListener] implement
     */
    override fun onFacePerformed() {
        RealSense.hasFace()
    }

    override fun onFaceLeaved() {
        captureTimer.onCancel()
        uiThread {
            listener.onCaptureTick(null)
            listener.onRecordMessage("Quý khách vui lòng đưa gương mặt vào vùng nhận diện")
        }
    }

    override fun onPortraitImage(bitmap: Bitmap) {

        invalidFaceCount.set(0)

        if (!hasDetect) return

        if (!captureTimer.isCountdown) {
            onPortraitValid()
            return
        }

        if (captureTimer.step < 1) {
            captureTimer.onCancel()
            hasDetect = false
            timeoutHandler.removeCallbacks(timeoutRunnable)
            uiThread {
                listener.onRecordMessage(null)
                listener.onPortraitCaptured(bitmap)
            }
        }
    }


    /**
     * Timeout handle
     */
    var timeout: Long = 0

    private val timeoutHandler: Handler = Handler(Looper.getMainLooper())

    private val timeoutRunnable = Runnable { listener.onCaptureTimeout() }

    /**
     *
     */
    private fun onPortraitValid() {
        captureTimer.onStart()
        uiThread {
            listener.onRecordMessage("Quý khách vui lòng giữ gương mặt\ntrong vùng nhận diện")
        }
    }

    private fun onPortraitInvalid(message: String?) {
        if (!captureTimer.isCountdown) return
        if (invalidFaceCount.incrementAndGet() < 3) return
        captureTimer.onCancel()
        uiThread {
            listener.onRecordMessage(message)
            listener.onCaptureTick(null)
        }
    }

    interface Listener {

        fun onCaptureTick(second: String?)

        fun onPortraitCaptured(image: Bitmap)

        fun onRecordMessage(message: String?)

        fun onCaptureTimeout()
    }

    inner class CaptureTimer : CountDownTimer(2000, 500) {

        var isCountdown: Boolean = false
            private set

        private val autoStep = AtomicInteger()

        val step: Int get() = autoStep.get()


        override fun onTick(second: Long) {
            if (isCountdown) {
                listener.onCaptureTick(autoStep.getAndDecrement().toString())
            }
            if (autoStep.get() < 1) {
                cancel()
            }
        }

        override fun onFinish() {
            isCountdown = false
            listener.onCaptureTick(null)
        }

        fun onStart() {
            isCountdown = true
            autoStep.set(3)
            start()
        }

        fun onCancel() {
            cancel()
            isCountdown = false
            listener.onCaptureTick(null)
        }
    }

}