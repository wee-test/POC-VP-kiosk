package wee.digital.camera.job

import android.graphics.Bitmap
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import org.opencv.core.Mat
import wee.digital.camera.RealSense
import wee.digital.camera.detector.Detection
import wee.digital.camera.detector.FaceDetector
import wee.digital.camera.detector.FaceDetector.Companion.MIN_BLUR
import wee.digital.camera.detector.FaceDetector.Companion.MIN_SCORE
import wee.digital.camera.uiThread
import wee.digital.camera.utils.OpenCVUtils
import wee.digital.camera.utils.RecordVideo
import java.util.concurrent.atomic.AtomicInteger

class DetectionJob(private val listener: FaceCaptureJob.Listener) :
        Detection.DetectionListener,
        FaceDetector.OptionListener {

    private var hasDetect: Boolean = false

    private val invalidFaceCount = AtomicInteger()

    private val captureTimer = DetectionTimer()

    private val detection: Detection = Detection().also {
        it.statusListener = this
        it.optionListener = this
    }

    private val imagesObserver = Observer<Pair<Bitmap, Mat>?> {
        it?.apply {
            if (hasDetect) detection.detectFace(first, second)
        }
    }

    fun observe(lifecycleOwner : LifecycleOwner){
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
        if (timeout > 0) timeOutHandler.removeCallbacks(timeoutRunnable)
        RealSense.imagesLiveData.observeForever(imagesObserver)
        hasDetect = true
    }

    fun pauseDetect() {
        hasDetect = false
        RealSense.imagesLiveData.removeObserver(imagesObserver)
        detection.destroy()
    }

    /**
     * implement listener detection
     */
    override fun hasFace() {
        RealSense.hasFace()
    }

    override fun faceLeaved() {
        captureTimer.onCancel()
        uiThread {
            pauseRecordVideo()
            listener.onCaptureTick(null)
            listener.onRecordMessage("Quý khách vui lòng đưa gương mặt vào vùng nhận diện")
        }
    }

    override fun faceChange() {
        captureTimer.onCancel()
        uiThread {
            pauseRecordVideo()
            listener.onCaptureTick(null)
            listener.onRecordMessage("Quý khách vui lòng đưa gương mặt vào vùng nhận diện")
        }
    }

    override fun faceEligible(face: Bitmap) {
        invalidFaceCount.set(0)

        if (!hasDetect) return

        if (!captureTimer.isCountdown) {
            onPortraitValid()
            return
        }

        if (captureTimer.step < 1) {
            captureTimer.onCancel()
            hasDetect = false
            timeOutHandler.removeCallbacks(timeoutRunnable)
            uiThread {
                listener.onRecordMessage(null)
                listener.onPortraitCaptured(face)
            }
        }

    }

    override fun mutilFace() {
        captureTimer.onCancel()
        uiThread {
            pauseRecordVideo()
            listener.onCaptureTick(null)
            listener.onRecordMessage("Có nhiều hơn 1 khuôn mặt trong vùng nhận diện")
        }
    }

    /**
     * implement listener FaceDetectionJob
     */
    override fun onFaceScore(score: Float): Boolean {
        Log.e("checkFace", "onFaceScore $score")
        return score > MIN_SCORE
    }

    override fun onFaceBlurred(faceCropped: Bitmap): Boolean {
        return OpenCVUtils.checkIfImageIsBlurred(faceCropped, MIN_BLUR)
    }

    override fun onDepthLabel(label: String, confidence: Float): Boolean {
        Log.e("checkFace", "onDepthLabel $label - $confidence")
        val isReal = super.onDepthLabel(label, confidence)
        if (!isReal) onPortraitInvalid("Khuôn mặt không hợp lệ")
        return isReal
    }

    override fun onFaceDegrees(x: Double, y: Double): Boolean {
        Log.e("checkFace", "onFaceDegrees $x - $y")
        val isValidDegrees = x in -20f..20f && y in -35f..25f
        if (!isValidDegrees) onPortraitInvalid("Góc mặt không hợp lệ")
        return isValidDegrees
    }

    /**
     * Timeout handler
     */
    var timeout: Long = 0

    private val timeOutHandler: Handler = Handler(Looper.getMainLooper())

    private val timeoutRunnable = Runnable { listener.onCaptureTimeout() }

    private fun onPortraitValid() {
        captureTimer.onStart()
        uiThread {
            listener.onRecordMessage("Quý khách vui lòng giữ gương mặt trong vùng nhận diện")
        }
    }

    private fun onPortraitInvalid(message: String) {
        if (!captureTimer.isCountdown) return
        if (invalidFaceCount.incrementAndGet() < 3) return
        captureTimer.onCancel()
        uiThread {
            listener.onRecordMessage(message)
            pauseRecordVideo()
            listener.onCaptureTick(null)
        }
    }

    inner class DetectionTimer : CountDownTimer(9000, 1000) {

        var isCountdown: Boolean = false
            private set

        private val autoStep = AtomicInteger()

        val step: Int get() = autoStep.get()

        override fun onTick(second: Long) {
            if (isCountdown) {
                val time = autoStep.getAndDecrement()
                listener.onCaptureTick(time.toString())
            }
            if (autoStep.get() < 0) {
                cancel()
            }
        }

        override fun onFinish() {
            isCountdown = false
            pauseRecordVideo()
            listener.onCaptureTick(null)
        }

        fun onStart() {
            isCountdown = true
            RecordVideo.arrayBitmap.clear()
            RecordVideo.isRecordVideo = true
            autoStep.set(8)
            start()
        }

        fun onCancel() {
            cancel()
            isCountdown = false
            pauseRecordVideo()
            listener.onCaptureTick(null)
        }

    }

    fun pauseRecordVideo() {
        RecordVideo.isRecordVideo = false
    }

}