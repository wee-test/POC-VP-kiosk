package wee.digital.camera.detector

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import org.opencv.core.Mat
import wee.digital.camera.*
import wee.digital.camera.detector.FaceDetector.Companion.MIN_SIZE
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Detection {

    private val mtcnn: MTCNN = MTCNN(RealSense.app.assets)

    private var isDetecting: Boolean = false

    private var currentFace: Box? = null

    private val executors: ExecutorService = Executors.newSingleThreadExecutor()

    var statusListener: DetectionListener? = null

    var optionListener: FaceDetector.OptionListener? = null

    fun detectFace(colorBitmap: Bitmap, depthBitmap: Mat) {
        if (isDetecting) return
        isDetecting = true
        mtcnn.detectFacesAsync(executors, colorBitmap, MIN_SIZE)
                .addOnCanceledListener { isDetecting = false }
                .addOnFailureListener {
                    executors.execute {
                        statusListener?.faceLeaved()
                        isDetecting = false
                    }
                }
                .addOnSuccessListener { result ->
                    executors.execute {
                        val box: Box? = result.largestBox()
                        if (box == null) {
                            statusListener?.faceLeaved()
                            isDetecting = false
                        } else {
                            val listBox = result.listBox()
                            if (listBox!!.size > 1) {
                                isDetecting = false
                                statusListener?.mutilFace()
                                return@execute
                            }
                            statusListener?.hasFace()
                            if (!faceChangeProcess(box)) {
                                statusListener?.faceChange()
                            }
                            currentFace = box
                            onFaceDetect(box, colorBitmap, depthBitmap)
                        }
                    }
                }
    }

    private fun onFaceDetect(box: Box, colorBitmap: Bitmap, depthBitmap: Mat) {
        optionListener ?: return
        if (!optionListener!!.onFaceScore(box.score)) {
            isDetecting = false
            return
        }

        val rectFace = box.transformToRect()

        if (!optionListener!!.onFaceRect(rectFace)) {
            isDetecting = false
            return
        }

        var degreesValid = false
        box.getDegrees { x, y -> degreesValid = optionListener!!.onFaceDegrees(x, y) }
        if (!degreesValid) {
            isDetecting = false
            return
        }

        val boxRect = box.transformToRect()
        boxRect.cropPortrait(colorBitmap)?.also {
            statusListener?.faceEligible(it)
        }
        isDetecting = false
    }

    fun destroy() {
        executors.shutdown()
        currentFace = null
    }

    private fun onBlurFaceDetect(boxRect: Rect, colorBitmap: Bitmap): Bitmap? {
        val faceBitmap = boxRect.cropFace(colorBitmap) ?: return null
        if (optionListener!!.onFaceBlurred(faceBitmap)) return null
        return faceBitmap
    }

    private fun faceChangeProcess(face: Box): Boolean {
        currentFace ?: return false
        val nowRect = face.transformToRect()
        val nowCenterX = nowRect.exactCenterX()
        val nowCenterY = nowRect.exactCenterY()
        val nowCenterPoint = PointF(nowCenterX, nowCenterY)
        val curRect = currentFace!!.transformToRect()
        val curCenterX = curRect.exactCenterX()
        val curCenterY = curRect.exactCenterY()
        val curCenterPoint = PointF(curCenterX, curCenterY)
        val dist = distancePoint(nowCenterPoint, curCenterPoint)
        return dist < FaceDetector.MIN_DISTANCE
    }

    interface DetectionListener {
        fun hasFace()
        fun faceLeaved()
        fun mutilFace()
        fun faceChange()
        fun faceEligible(face: Bitmap)
    }

}