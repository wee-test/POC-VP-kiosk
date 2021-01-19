package wee.digital.camera.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.core.Core.inRange
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

object OpenCVUtils {
    private val TAG = OpenCVUtils::class.java.simpleName
    private var openCVInitialized = false

    fun initOpenCV(context: Context) {
        if (openCVInitialized) return
        val loaderCallback = object : BaseLoaderCallback(context) {
            override fun onManagerConnected(status: Int) {
                when (status) {
                    LoaderCallbackInterface.SUCCESS -> {
                        openCVInitialized = true
                    }
                    else -> {
                        openCVInitialized = false
                        super.onManagerConnected(status)
                    }
                }
            }
        }
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, loaderCallback)
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    fun convertBlackWhite(bitmap: Bitmap): Bitmap {
        // first convert bitmap into OpenCV mat object
        val imageMat = Mat(bitmap.height, bitmap.width, CvType.CV_8U, Scalar(4.0))
        val myBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(myBitmap, imageMat)
        // now convert to gray
        cvtColor(imageMat, imageMat, COLOR_RGB2GRAY, 1)
        inRange(imageMat, Scalar(0.0, 0.0, 200.0, 0.0), Scalar(180.0, 255.0, 255.0, 0.0), imageMat)
        threshold(imageMat, imageMat, 212.0, 255.0, THRESH_BINARY)
        threshold(imageMat, imageMat, 0.0, 255.0, THRESH_BINARY + THRESH_OTSU)
        GaussianBlur(imageMat, imageMat, Size(4.0, 4.0), 0.0)
        // Imgproc.medianBlur(imageMat, imageMat, 3)

        // get the thresholded image
        val thresholdMat = Mat(bitmap.height, bitmap.width, CvType.CV_8U, Scalar(1.0))
        threshold(imageMat, thresholdMat, 180.0, 255.0, THRESH_TOZERO)
        // convert back to bitmap for displaying

        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        thresholdMat.convertTo(thresholdMat, CvType.CV_8UC1)
        Utils.matToBitmap(thresholdMat, resultBitmap)
        return resultBitmap
    }

    fun convertNV21OpenCV(data: ByteArray, width: Int, height: Int): Bitmap? {
        return try {
            val yuv = Mat(height + height / 2, width, CvType.CV_8UC1)
            yuv.put(0, 0, data)
            val rgb = Mat()
            cvtColor(yuv, rgb, COLOR_YUV2RGBA_NV21, 4)
            val bitmap = Bitmap.createBitmap(rgb.width(), rgb.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(rgb, bitmap)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


    private fun angle(p1: Point, p2: Point, p0: Point): Double {
        val dx1 = p1.x - p0.x
        val dy1 = p1.y - p0.y
        val dx2 = p2.x - p0.x
        val dy2 = p2.y - p0.y
        return ((dx1 * dx2 + dy1 * dy2)
                / sqrt(
                (dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
                        + 1e-10
        ))
    }


    private fun matToBitmap(mat: Mat): Bitmap {
        val bitmap =
                Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }

    private fun euclideanDistance(a: Point, b: Point): Double {
        var distance = 0.0
        try {
            val xDiff = a.x - b.x
            val yDiff = a.y - b.y
            distance = Math.sqrt(Math.pow(xDiff, 2.0) + Math.pow(yDiff, 2.0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return distance
    }

    private fun sortListPoint(points: ArrayList<Point>): ArrayList<Point> {
        // # initialize a list of coordinates that will be ordered
        // # such that the first entry in the list is the top-left,
        // # the second entry is the top-right, the third is the
        // # bottom-right, and the fourth is the bottom-left
        Collections.sort(points) { p1, p2 ->
            val s1 = p1.x + p1.y
            val s2 = p2.x + p2.y
            s1.compareTo(s2)
        }
        val topLeft = points[0]
        val bottomRight = points[3]
        // # now, compute the difference between the points, the
        // # top-right point will have the smallest difference,
        // # whereas the bottom-left will have the largest difference
        Collections.sort(points) { p1, p2 ->
            val s1 = p1.y - p1.x
            val s2 = p2.y - p2.x
            s1.compareTo(s2)
        }
        val topRight = points[0]
        val bottomLeft = points[3]
        return arrayListOf(topLeft, topRight, bottomRight, bottomLeft)
    }

    fun checkIfImageIsBlurred(bitmap: Bitmap?, minValueBlur: Double): Boolean {
        if (bitmap == null) {
            //Log.e("Expected bitmap was null",);
            return false
        }

        val imageBitmapMat = Mat(bitmap.width, bitmap.height, CvType.CV_8UC1)
        Utils.bitmapToMat(bitmap, imageBitmapMat)

        val grayscaleBitmapMat = Mat()
        Imgproc.cvtColor(imageBitmapMat, grayscaleBitmapMat, Imgproc.COLOR_RGB2GRAY)

        val postLaplacianMat = Mat()
        Imgproc.Laplacian(grayscaleBitmapMat, postLaplacianMat, 3)

        val mean = MatOfDouble()
        val standardDeviation = MatOfDouble()
        Core.meanStdDev(postLaplacianMat, mean, standardDeviation)

        val result = standardDeviation.get(0, 0)[0].pow(2.0)
        Log.e("blurry result", "" + result)
        return result < minValueBlur
    }

    fun checkFaceIsBlurred(bitmap: Bitmap?, rect: Rect, minValueBlur: Double): Boolean {
        bitmap ?: return true
        return try{
            val cropped = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            checkIfImageIsBlurred(cropped, minValueBlur)
        }catch (e: Exception){
            false
        }

    }
}