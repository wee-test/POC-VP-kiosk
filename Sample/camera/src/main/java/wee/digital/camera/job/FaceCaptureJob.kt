package wee.digital.camera.job

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import wee.digital.camera.RealSense
import wee.digital.camera.detector.FaceDetector
import wee.digital.camera.detector.FaceDetector.Companion.MIN_BLUR
import wee.digital.camera.detector.FaceDetector.Companion.MIN_SCORE
import wee.digital.camera.uiThread
import wee.digital.camera.utils.OpenCVUtils
import wee.digital.camera.utils.RecordVideo
import java.util.concurrent.atomic.AtomicInteger


class FaceCaptureJob(private val listener: Listener) :
        FaceDetector.DataListener,
        FaceDetector.OptionListener,
        FaceDetector.StatusListener {

    companion object {
        const val MIN_SIZE = 180
        const val MAX_SIZE = 680
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
        Log.e("CheckFace", "onFaceScore $score")
        return score > MIN_SCORE
    }

    override fun onFaceBlurred(faceCropped: Bitmap): Boolean {
        /*val isBlur = OpenCVUtils.checkIfImageIsBlurred(faceCropped, MIN_BLUR)
        if(isBlur){
            listener.onWarningMessage("Hình ảnh khuôn mặt không đủ điều kiện")
        }*/
        return OpenCVUtils.checkIfImageIsBlurred(faceCropped, MIN_BLUR)
    }

    override fun onDepthLabel(label: String, confidence: Float): Boolean {
        Log.e("CheckFace", "onDepthLabel $label - $confidence")
        val isReal = super.onDepthLabel(label, confidence)
        if (!isReal) onPortraitInvalid("Khuôn mặt không hợp lệ")
        return isReal
    }

    override fun onFaceDegrees(x: Double, y: Double): Boolean {
        Log.e("CheckFace", "onFaceDegrees $x - $y")
        val isValidDegrees = x in -20f..20f && y in -35f..25f
        if (!isValidDegrees) onPortraitInvalid("Góc mặt không hợp lệ")
        return isValidDegrees
    }

    override fun onFaceRect(faceRect: Rect): Boolean {
        Log.e("CheckFace", "onFaceRect w: ${faceRect.width()} - ${faceRect.centerX()}x${faceRect.centerY()}")
        return when (faceRect.width()) {
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

    override fun onFaceChanged() {
        captureTimer.onCancel()
        uiThread {
            pauseRecordVideo()
            listener.onCaptureTick(null)
            listener.onRecordMessage("Quý khách vui lòng đưa gương mặt vào vùng nhận diện")
        }
        super.onFaceChanged()
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
            pauseRecordVideo()
            listener.onCaptureTick(null)
            listener.onRecordMessage("Quý khách vui lòng đưa gương mặt vào vùng nhận diện")
        }
    }

    override fun onManyFaces() {
        captureTimer.onCancel()
        uiThread {
            pauseRecordVideo()
            listener.onCaptureTick(null)
            listener.onWarningMessage("Có nhiều hơn 1 khuôn mặt trong vùng nhận diện")
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
            listener.onRecordMessage("Quý khách vui lòng giữ gương mặt trong vùng nhận diện")
        }
    }

    private fun onPortraitInvalid(message: String?) {
        if (!captureTimer.isCountdown) return
        if (invalidFaceCount.incrementAndGet() < 3) return
        captureTimer.onCancel()
        uiThread {
            listener.onRecordMessage(message)
            pauseRecordVideo()
            listener.onCaptureTick(null)
        }
    }

    interface Listener {

        fun onCaptureTick(second: String?)

        fun onPortraitCaptured(image: Bitmap)

        fun onRecordMessage(message: String?)

        fun onWarningMessage(message: String)

        fun onCaptureTimeout()
    }

    inner class CaptureTimer : CountDownTimer(4000, 500) {

        var isCountdown: Boolean = false
            private set

        private val autoStep = AtomicInteger()

        val step: Int get() = autoStep.get()


        override fun onTick(second: Long) {
            if (isCountdown) {
                RecordVideo.isRecordVideo = true
                listener.onCaptureTick(autoStep.getAndDecrement().toString())
            }
            if (autoStep.get() < 1) {
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
            autoStep.set(7)
            start()
        }

        fun onCancel() {
            cancel()
            isCountdown = false
            pauseRecordVideo()
            listener.onCaptureTick(null)
        }
    }

    fun pauseRecordVideo(){
        RecordVideo.arrayBitmap.clear()
        RecordVideo.isRecordVideo = false
    }

}