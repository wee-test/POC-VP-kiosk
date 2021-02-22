package wee.digital.camera.utils

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
import org.opencv.imgproc.Imgproc.*
import org.opencv.utils.Converters
import java.util.Collections.sort
import kotlin.math.abs
import kotlin.math.atan2
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

    fun angleLine(p: Point): Float {
        var angle = Math.toDegrees(atan2(p.y, p.x)).toFloat()

        if (angle < 0) {
            angle += 360f
        }
        Log.e("angleLine","$angle")
        return angle
    }

    fun checkIfImageIsBlurred(bitmap: Bitmap?, minValueBlur: Double): Boolean {
        if (bitmap == null) {
            //Log.e("Expected bitmap was null",);
            return false
        }

        val imageBitmapMat = Mat(bitmap.width, bitmap.height, CvType.CV_8UC1)
        Utils.bitmapToMat(bitmap, imageBitmapMat)

        val grayscaleBitmapMat = Mat()
        cvtColor(imageBitmapMat, grayscaleBitmapMat, COLOR_RGB2GRAY)

        val postLaplacianMat = Mat()
        Laplacian(grayscaleBitmapMat, postLaplacianMat, 10)
        val mean = MatOfDouble()
        val standardDeviation = MatOfDouble()
        Core.meanStdDev(postLaplacianMat, mean, standardDeviation)

        val result = standardDeviation.get(0, 0)[0].pow(2.0)
        Log.e("blurryResult", "$result / $minValueBlur")
        imageBitmapMat.release()
        grayscaleBitmapMat.release()
        postLaplacianMat.release()
        mean.release()
        standardDeviation.release()
        return result < minValueBlur
    }

    fun checkFaceIsBlurred(bitmap: Bitmap?, rect: Rect, minValueBlur: Double): Boolean {
        bitmap ?: return true
        return try {
            val cropped = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            checkIfImageIsBlurred(cropped, minValueBlur)
        } catch (e: Exception) {
            false
        }

    }

    fun cropObjectRect(data: ByteArray, width: Int, height: Int): Bitmap? {
        return try {
            val yuv = Mat(height + height / 2, width, CvType.CV_8UC1)
            yuv.put(0, 0, data)
            val rgb = Mat()
            cvtColor(yuv, rgb, COLOR_YUV2RGBA_NV21, 4)
            yuv.release()
            return findRectangleRect(rgb)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun findRectangleRect(src: Mat): Bitmap? {
        var bitmapCrop: Bitmap? = null
        val ratio: Double = 480.0 / src.width().coerceAtLeast(src.height())
        val downscaledSize = Size(src.width() * ratio, src.height() * ratio)
        val srcResize = Mat(downscaledSize, src.type())
        resize(src, srcResize, downscaledSize)
        cvtColor(srcResize, srcResize, COLOR_BGR2GRAY)
        val blurred = Mat(srcResize.rows(),srcResize.cols(), CvType.CV_8UC1)
        //srcResize.convertTo(blurred, CV_8UC1)
        //bilateralFilter(blurred,blurred,20,170.0,170.0)
        medianBlur(srcResize, blurred, 5)
        val adaptiveThresh = Mat()
        val contours: ArrayList<MatOfPoint> = ArrayList()
        val approxCurve = MatOfPoint2f()
        var maxContour : MatOfPoint? = null

        adaptiveThreshold(
                blurred, adaptiveThresh,255.0 ,
                ADAPTIVE_THRESH_GAUSSIAN_C,
                THRESH_BINARY,
                255, 32.0)
        //val edged = Mat()
        //Canny(adaptiveThresh,edged,1.0,200.0)
        val tempContours = Mat()
        findContours(adaptiveThresh, contours, tempContours, RETR_LIST, CHAIN_APPROX_SIMPLE)
        for(contour in contours){
            val temp = MatOfPoint2f(*contour.toArray())
            approxPolyDP(temp, approxCurve, arcLength(temp, true) * 0.02, true)
            if (approxCurve.total() == 4L) {
                try{
                    var maxCosine = 0.0
                    val curves: List<Point> = approxCurve.toList()
                    for (j in 2..4) {
                        val cosine = abs(angle(curves[j % 4], curves[j - 2], curves[j - 1]))
                        maxCosine = maxCosine.coerceAtLeast(cosine)
                    }
                    if (maxCosine < 0.45) {
                        maxContour = contour
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            temp.release()
        }

        bitmapCrop = try{
            if (maxContour !=null) {
                val rect = boundingRect(maxContour)
                val reSizeRect = org.opencv.core.Rect((rect.x / ratio).toInt(), (rect.y / ratio).toInt(),
                        (rect.width / ratio).toInt(), (rect.height / ratio).toInt())
                if(reSizeRect.height> 400) {
                    val cropMat = src.submat(reSizeRect)
                    matToBitmap(cropMat)
                }else{
                    null
                }
            }else{
                null
            }
        }catch (e: Exception){
            null
        }
        tempContours.release()
        approxCurve.release()
        srcResize.release()
        blurred.release()
        adaptiveThresh.release()
        src.release()
        return bitmapCrop
    }

    @Throws(Exception::class)
    private fun findRectangle(src: Mat): Bitmap? {
        var bitmapCrop: Bitmap? = null
        // STEP 1: Resize input image to img_proc to reduce computation
        val ratio: Double = 480.0 / src.width().coerceAtLeast(src.height())
        val downscaledSize = Size(src.width() * ratio, src.height() * ratio)
        val srcResize = Mat(downscaledSize, src.type())
        resize(src, srcResize, downscaledSize)
        //val srcBW = Mat()
        cvtColor(srcResize, srcResize, COLOR_BGR2GRAY)
        //threshold(srcResize, srcBW, THRESH_CROP, MAX_VAL, THRESH_BINARY_INV)

        val blurred = srcResize.clone()
        medianBlur(srcResize, blurred, 5)
        val grayBlurred = Mat(blurred.size(), CvType.CV_8U)
        val grayCanny = Mat()
        val contours: ArrayList<MatOfPoint> = ArrayList()
        val blurredChannel: MutableList<Mat> = ArrayList()
        blurredChannel.add(blurred)
        val gray0Channel: MutableList<Mat> = ArrayList()
        gray0Channel.add(grayBlurred)
        var approxCurve: MatOfPoint2f? = null
        var maxCurve = MatOfPoint2f()
        var maxArea = 0.0
        var maxId = -1
        val threshold = 10.0
        for (c in 0..0) {
            val ch = intArrayOf(c, 0)
            Core.mixChannels(blurredChannel, gray0Channel, MatOfInt(*ch))
            val thresholdLevel = 1
            for (t in 0 until thresholdLevel) {
                if (t == 0) {
                    Canny(grayBlurred, grayCanny, 0.0, threshold * 4, 3, true) // true ?
                    val tempDilate = Mat()
                    dilate(
                            grayCanny,
                            grayCanny,
                            tempDilate,
                            Point(-1.0, -1.0),
                            1
                    ) // 1
                    tempDilate.release()
                } else {
                    adaptiveThreshold(
                            grayBlurred, grayCanny, thresholdLevel.toDouble(),
                            ADAPTIVE_THRESH_GAUSSIAN_C,
                            THRESH_BINARY,
                            (srcResize.width() + srcResize.height()) / 200, t.toDouble()
                    )
                }
                val tempContours = Mat()
                findContours(
                        grayCanny, contours, tempContours,
                        RETR_LIST, CHAIN_APPROX_SIMPLE
                )

                for (contour in contours) {
                    val temp = MatOfPoint2f(*contour.toArray())
                    val area = contourArea(contour)
                    approxCurve = MatOfPoint2f()
                    approxPolyDP(
                            temp, approxCurve,
                            arcLength(temp, true) * 0.02, true
                    )
                    if (approxCurve.total() == 4L && area > maxArea) {
                        var maxCosine = 0.0
                        val curves: List<Point> = approxCurve.toList()
                        for (j in 2..4) {
                            val cosine = abs(
                                    angle(
                                            curves[j % 4],
                                            curves[j - 2],
                                            curves[j - 1]
                                    )
                            )
                            maxCosine = maxCosine.coerceAtLeast(cosine)
                        }
                        if (maxCosine < 0.1) {
                            maxArea = area
                            maxCurve = approxCurve
                            maxId = contours.indexOf(contour)
                        }
                    }
                    temp.release()
                }
                tempContours.release()
            }
        }

        if (maxId >= 0) {
            //drawContours(grayCanny, contours, maxId, Scalar(255.0, 0.0, 0.0), 10)

            var tempDouble = maxCurve[0, 0]
            val p1 = Point(tempDouble[0], tempDouble[1])

            tempDouble = maxCurve[1, 0]
            val p2 = Point(tempDouble[0], tempDouble[1])

            tempDouble = maxCurve[2, 0]
            val p3 = Point(tempDouble[0], tempDouble[1])

            tempDouble = maxCurve[3, 0]
            val p4 = Point(tempDouble[0], tempDouble[1])

            val listPointSRC = sortListPoint(arrayListOf(p1, p2, p3, p4))
            /*line(srcResize, listPointSRC[0], listPointSRC[1], Scalar(250.0, 0.0, 0.0), 10)
            line(srcResize, listPointSRC[0], listPointSRC[3], Scalar(250.0, 0.0, 0.0), 10)
            line(srcResize, listPointSRC[1], listPointSRC[2], Scalar(250.0, 0.0, 0.0), 10)
            line(srcResize, listPointSRC[3], listPointSRC[2], Scalar(250.0, 0.0, 0.0), 10)*/

            for (point in listPointSRC) {
                point.x = point.x / ratio
                point.y = point.y / ratio
            }

            val listDistance = arrayListOf<Double>()
            listDistance.add(euclideanDistance(listPointSRC[0], listPointSRC[1]))
            listDistance.add(euclideanDistance(listPointSRC[1], listPointSRC[2]))
            listDistance.add(euclideanDistance(listPointSRC[2], listPointSRC[3]))
            listDistance.add(euclideanDistance(listPointSRC[3], listPointSRC[0]))


            listDistance.sortDescending()
            val w = listDistance[0]
            val h = listDistance[2]

            if (h >= 400) {
                val op1 = Point(0.0, 0.0)
                val op2 = Point(w, 0.0)
                val op3 = Point(w, h)
                val op4 = Point(0.0, h)
                val outM = Mat(w.toInt(), h.toInt(), CvType.CV_8UC4)
                val listPointDES = arrayListOf(op1, op2, op3, op4)

                val startM = Converters.vector_Point2f_to_Mat(listPointSRC)
                val endM = Converters.vector_Point2f_to_Mat(listPointDES)

                val perspectiveTransform = getPerspectiveTransform(startM, endM)
                warpPerspective(
                        src,
                        outM,
                        perspectiveTransform,
                        Size(w, h)
                )
                bitmapCrop = matToBitmap(outM)
                outM.release()
                startM.release()
                endM.release()
                perspectiveTransform.release()
            }
        }

        maxCurve.release()
        approxCurve?.release()

        srcResize.release()
        blurred.release()
        grayBlurred.release()
        grayCanny.release()
        src.release()
        blurredChannel.forEach { it.release() }
        gray0Channel.forEach { it.release() }
        approxCurve?.release()
        maxCurve.release()
        return bitmapCrop
    }

    private fun euclideanDistance(a: Point, b: Point): Double {
        var distance = 0.0
        try {
            val xDiff = a.x - b.x
            val yDiff = a.y - b.y
            distance = sqrt(xDiff.pow(2.0) + yDiff.pow(2.0))
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
        sort(points) { p1, p2 ->
            val s1 = p1.x + p1.y
            val s2 = p2.x + p2.y
            s1.compareTo(s2)
        }
        val topLeft = points[0]
        val bottomRight = points[3]
        // # now, compute the difference between the points, the
        // # top-right point will have the smallest difference,
        // # whereas the bottom-left will have the largest difference
        sort(points) { p1, p2 ->
            val s1 = p1.y - p1.x
            val s2 = p2.y - p2.x
            s1.compareTo(s2)
        }
        val topRight = points[0]
        val bottomLeft = points[3]
        return arrayListOf(topLeft, topRight, bottomRight, bottomLeft)
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
}

