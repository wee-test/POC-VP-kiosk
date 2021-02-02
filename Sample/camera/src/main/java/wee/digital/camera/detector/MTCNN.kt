package wee.digital.camera.detector

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
  MTCNN For Android
  by cjf@xmu 20180625
 */
class MTCNN internal constructor(private val assetManager: AssetManager) {
    //参数
    private val factor = 0.709f
    private val PNetThreshold = 0.6f
    private val RNetThreshold = 0.7f
    private val ONetThreshold = 0.7f

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    var lastProcessTime //lastImageProcessingTimeMs
            : Long = 0
    private var inferenceInterface: TensorFlowInferenceInterface? = null
    private fun loadModel(): Boolean { //AssetManager
        try {
            inferenceInterface = TensorFlowInferenceInterface(assetManager, MODEL_FILE)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    //Read Bitmap pixel values, preprocess (-127.5 / 128), convert to a one-dimensional array and return
    private fun normalizeImage(bitmap: Bitmap): FloatArray {
        val w = bitmap.width
        val h = bitmap.height
        val floatValues = FloatArray(w * h * 3)
        val intValues = IntArray(w * h)
        bitmap.getPixels(
                intValues,
                0,
                bitmap.width,
                0,
                0,
                bitmap.width,
                bitmap.height
        )
        val imageMean = 127.5f
        val imageStd = 128f
        for (i in intValues.indices) {
            val `val` = intValues[i]
            floatValues[i * 3 + 0] = ((`val` shr 16 and 0xFF) - imageMean) / imageStd
            floatValues[i * 3 + 1] = ((`val` shr 8 and 0xFF) - imageMean) / imageStd
            floatValues[i * 3 + 2] = ((`val` and 0xFF) - imageMean) / imageStd
        }
        return floatValues
    }

    /*
       Detect faces, minSize is the smallest face pixel value
     */
    private fun bitmapResize(bm: Bitmap, scale: Float): Bitmap {
        val width = bm.width
        val height = bm.height
        // CREATE A MATRIX FOR THE MANIPULATION。matrix指定图片仿射变换参数
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true
        )
    }

    //flipBeforeInputAndFlipOutput
    private fun PNetForward(
            bitmap: Bitmap,
            PNetOutProb: Array<FloatArray>,
            PNetOutBias: Array<Array<FloatArray>>
    ): Int {
        val w = bitmap.width
        val h = bitmap.height
        val PNetIn = normalizeImage(bitmap)
        flipDiagonal(PNetIn, h, w, 3) //沿着对角线翻转
        inferenceInterface!!.feed(PNetInName, PNetIn, 1, w.toLong(), h.toLong(), 3)
        inferenceInterface!!.run(PNetOutName, false)
        val PNetOutSizeW = Math.ceil(w * 0.5 - 5).toInt()
        val PNetOutSizeH = Math.ceil(h * 0.5 - 5).toInt()
        val PNetOutP = FloatArray(PNetOutSizeW * PNetOutSizeH * 2)
        val PNetOutB = FloatArray(PNetOutSizeW * PNetOutSizeH * 4)
        inferenceInterface!!.fetch(PNetOutName[0], PNetOutP)
        inferenceInterface!!.fetch(PNetOutName[1], PNetOutB)
        //【写法一】先翻转，后转为2/3维数组
        flipDiagonal(PNetOutP, PNetOutSizeW, PNetOutSizeH, 2)
        flipDiagonal(PNetOutB, PNetOutSizeW, PNetOutSizeH, 4)
        expand(PNetOutB, PNetOutBias)
        expandProb(PNetOutP, PNetOutProb)
        /*
        *【写法二】这个比较快，快了3ms。意义不大，用上面的方法比较直观
        for (int y=0;y<PNetOutSizeH;y++)
            for (int x=0;x<PNetOutSizeW;x++){
               int idx=PNetOutSizeH*x+y;
               PNetOutProb[y][x]=PNetOutP[idx*2+1];
               for(int i=0;i<4;i++)
                   PNetOutBias[y][x][i]=PNetOutB[idx*4+i];
            }
        */return 0
    }

    //Non-Maximum Suppression
//nms，不符合条件的deleted设置为true
    private fun nms(
            boxes: Vector<Box>,
            threshold: Float,
            method: String
    ) { //NMS.两两比对
//int delete_cnt=0;

        for (i in boxes.indices) {
            val box = boxes[i]
            if (!box.deleted) { //score<0表示当前矩形框被删除
                for (j in i + 1 until boxes.size) {
                    val box2 = boxes[j]
                    if (!box2.deleted) {
                        val x1 = Math.max(box.box[0], box2.box[0])
                        val y1 = Math.max(box.box[1], box2.box[1])
                        val x2 = Math.min(box.box[2], box2.box[2])
                        val y2 = Math.min(box.box[3], box2.box[3])
                        if (x2 < x1 || y2 < y1) continue
                        val areaIoU = (x2 - x1 + 1) * (y2 - y1 + 1)
                        var iou = 0f
                        if (method == "Union") iou =
                                1.0f * areaIoU / (box.area() + box2.area() - areaIoU) else if (method == "Min") {
                            iou = 1.0f * areaIoU / Math.min(box.area(), box2.area())

                        }
                        if (iou >= threshold) { //删除prob小的那个框
                            if (box.score > box2.score) box2.deleted = true else box.deleted = true
                            //delete_cnt++;
                        }
                    }
                }
            }
        }
    }

    private fun generateBoxes(
            prob: Array<FloatArray>,
            bias: Array<Array<FloatArray>>,
            scale: Float,
            threshold: Float,
            boxes: Vector<Box>
    ): Int {
        val h = prob.size
        val w: Int = prob[0].size

        for (y in 0 until h) for (x in 0 until w) {
            val score = prob[y][x]
            //only accept prob >threadshold(0.6 here)
            if (score > threshold) {
                val box = Box()
                //score
                box.score = score
                //box
                box.box[0] = Math.round(x * 2 / scale)
                box.box[1] = Math.round(y * 2 / scale)
                box.box[2] = Math.round((x * 2 + 11) / scale)
                box.box[3] = Math.round((y * 2 + 11) / scale)
                //bbr
                for (i in 0..3) box.bbr[i] = bias[y][x][i]
                //add
                boxes.addElement(box)
            }
        }
        return 0
    }

    private fun BoundingBoxReggression(boxes: Vector<Box>) {
        for (i in boxes.indices) boxes[i].calibrate()
    }

    //Pnet + Bounding Box Regression + Non-Maximum Regression
/* NMS执行完后，才执行Regression
     * (1) For each scale , use NMS with threshold=0.5
     * (2) For all candidates , use NMS with threshold=0.7
     * (3) Calibrate Bounding Box
     * 注意：CNN输入图片最上面一行，坐标为[0..width,0]。所以Bitmap需要对折后再跑网络;网络输出同理.
     */
    private fun PNet(
            bitmap: Bitmap,
            minSize: Int
    ): Vector<Box> {
        val whMin = Math.min(bitmap.width, bitmap.height)
        var currentFaceSize =
                minSize.toFloat() //currentFaceSize=minSize/(factor^k) k=0,1,2... until excced whMin
        val totalBoxes =
                Vector<Box>()
        //【1】Image Paramid and Feed to Pnet
        while (currentFaceSize <= whMin) {
            val scale = 12.0f / currentFaceSize
            //(1)Image Resize
            val bm = bitmapResize(bitmap, scale)
            val w = bm.width
            val h = bm.height
            //(2)RUN CNN
            val PNetOutSizeW = (Math.ceil(w * 0.5 - 5) + 0.5).toInt()
            val PNetOutSizeH = (Math.ceil(h * 0.5 - 5) + 0.5).toInt()
            val PNetOutProb =
                    Array(PNetOutSizeH) { FloatArray(PNetOutSizeW) }
            val PNetOutBias =
                    Array(
                            PNetOutSizeH
                    ) { Array(PNetOutSizeW) { FloatArray(4) } }
            PNetForward(bm, PNetOutProb, PNetOutBias)
            //(3)数据解析
            val curBoxes =
                    Vector<Box>()
            generateBoxes(PNetOutProb, PNetOutBias, scale, PNetThreshold, curBoxes)

//(4)nms 0.5
            nms(curBoxes, 0.5f, "Union")
            //(5)add to totalBoxes
            for (i in curBoxes.indices) if (!curBoxes[i].deleted) totalBoxes.addElement(
                    curBoxes[i]
            )
            //Face Size等比递增
            currentFaceSize /= factor
        }
        //NMS 0.7
        nms(totalBoxes, 0.7f, "Union")
        //BBR
        BoundingBoxReggression(totalBoxes)
        return updateBoxes(totalBoxes)
    }

    //截取box中指定的矩形框(越界要处理)，并resize到size*size大小，返回数据存放到data中。
    var tmp_bm: Bitmap? = null

    private fun crop_and_resize(
            bitmap: Bitmap,
            box: Box,
            size: Int,
            data: FloatArray
    ) { //(2)crop and resize
        val matrix = Matrix()
        val scale = 1.0f * size / box.width()
        matrix.postScale(scale, scale)
        val croped = Bitmap.createBitmap(
                bitmap,
                box.left(),
                box.top(),
                box.width(),
                box.height(),
                matrix,
                true
        )
        //(3)save
        val pixels_buf = IntArray(size * size)
        croped.getPixels(
                pixels_buf,
                0,
                croped.width,
                0,
                0,
                croped.width,
                croped.height
        )
        val imageMean = 127.5f
        val imageStd = 128f
        for (i in pixels_buf.indices) {
            val `val` = pixels_buf[i]
            data[i * 3 + 0] = ((`val` shr 16 and 0xFF) - imageMean) / imageStd
            data[i * 3 + 1] = ((`val` shr 8 and 0xFF) - imageMean) / imageStd
            data[i * 3 + 2] = ((`val` and 0xFF) - imageMean) / imageStd
        }
    }

    /*
     * RNET跑神经网络，将score和bias写入boxes
     */
    private fun RNetForward(
            RNetIn: FloatArray,
            boxes: Vector<Box>
    ) {
        val num = RNetIn.size / 24 / 24 / 3
        //feed & run
        inferenceInterface!!.feed(RNetInName, RNetIn, num.toLong(), 24, 24, 3)
        inferenceInterface!!.run(RNetOutName, false)
        //fetch
        val RNetP = FloatArray(num * 2)
        val RNetB = FloatArray(num * 4)
        inferenceInterface!!.fetch(RNetOutName[0], RNetP)
        inferenceInterface!!.fetch(RNetOutName[1], RNetB)
        //转换
        for (i in 0 until num) {
            boxes[i].score = RNetP[i * 2 + 1]
            for (j in 0..3) boxes[i].bbr[j] = RNetB[i * 4 + j]
        }
    }

    //Refine Net
    private fun RNet(
            bitmap: Bitmap,
            boxes: Vector<Box>
    ): Vector<Box> { //RNet Input Init
        val num = boxes.size
        val RNetIn = FloatArray(num * 24 * 24 * 3)
        val curCrop = FloatArray(24 * 24 * 3)
        var RNetInIdx = 0
        for (i in 0 until num) {
            crop_and_resize(bitmap, boxes[i], 24, curCrop)
            flipDiagonal(curCrop, 24, 24, 3)
            for (j in curCrop.indices) RNetIn[RNetInIdx++] = curCrop[j]
        }
        //Run RNet
        RNetForward(RNetIn, boxes)
        //RNetThreshold
        for (i in 0 until num) if (boxes[i].score < RNetThreshold) boxes[i].deleted = true
        //Nms
        nms(boxes, 0.7f, "Union")
        BoundingBoxReggression(boxes)
        return updateBoxes(boxes)
    }

    /*
     * ONet跑神经网络，将score和bias写入boxes
     */
    private fun ONetForward(
            ONetIn: FloatArray,
            boxes: Vector<Box>
    ) {
        val num = ONetIn.size / 48 / 48 / 3
        //feed & run
        inferenceInterface!!.feed(ONetInName, ONetIn, num.toLong(), 48, 48, 3)
        inferenceInterface!!.run(ONetOutName, false)
        //fetch
        val ONetP = FloatArray(num * 2) //prob
        val ONetB = FloatArray(num * 4) //bias
        val ONetL = FloatArray(num * 10) //landmark
        try {
            inferenceInterface!!.fetch(ONetOutName[0], ONetP)
            inferenceInterface!!.fetch(ONetOutName[1], ONetB)
            inferenceInterface!!.fetch(ONetOutName[2], ONetL)
        } catch (e: Exception) {

        }
        //转换
        for (i in 0 until num) { //prob
            boxes[i].score = ONetP[i * 2 + 1]
            //bias
            for (j in 0..3) boxes[i].bbr[j] = ONetB[i * 4 + j]
            //landmark
            for (j in 0..4) {
                val x =
                        boxes[i].left() + (ONetL[i * 10 + j] * boxes[i].width()).toInt()
                val y =
                        boxes[i].top() + (ONetL[i * 10 + j + 5] * boxes[i].height()).toInt()
                boxes[i].landmark[j] = Point(x, y)
            }
        }
    }

    //ONet
    private fun ONet(
            bitmap: Bitmap,
            boxes: Vector<Box>
    ): Vector<Box> { //ONet Input Init
        val num = boxes.size
        val ONetIn = FloatArray(num * 48 * 48 * 3)
        val curCrop = FloatArray(48 * 48 * 3)
        var ONetInIdx = 0
        for (i in 0 until num) {
            crop_and_resize(bitmap, boxes[i], 48, curCrop)
            flipDiagonal(curCrop, 48, 48, 3)
            for (j in curCrop.indices) ONetIn[ONetInIdx++] = curCrop[j]
        }
        //Run ONet
        ONetForward(ONetIn, boxes)
        //ONetThreshold
        for (i in 0 until num) if (boxes[i].score < ONetThreshold) boxes[i].deleted = true
        BoundingBoxReggression(boxes)
        //Nms
        nms(boxes, 0.7f, "Min")
        return updateBoxes(boxes)
    }

    private fun square_limit(
            boxes: Vector<Box>,
            w: Int,
            h: Int
    ) { //square
        for (i in boxes.indices) {
            boxes[i].toSquareShape()
            boxes[i].limit_square(w, h)
        }
    }

    /*
     * 参数：
     *   bitmap:要处理的图片
     *   minFaceSize:最小的人脸像素值.(此值越大，检测越快)
     * 返回：
     *   人脸框
     */

    fun detectFacesAsync(bitmap: Bitmap, minFaceSize: Int): Task<Vector<Box>?> {
        return try {
            val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            Tasks.call(
                    executorService,
                    Callable<Vector<Box>> { detectFaces(bitmapCopy, minFaceSize) })
        } catch (e: Exception) {
            Tasks.call(executorService, Callable<Vector<Box>> { detectFaces(null, minFaceSize) })
        }


    }

    fun detectFacesAsync(executors: ExecutorService, bitmap: Bitmap, minFaceSize: Int): Task<Vector<Box>?> {
        return try {
            val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            Tasks.call(
                    executors,
                    Callable<Vector<Box>> { detectFaces(bitmapCopy, minFaceSize) })
        } catch (e: Exception) {
            Tasks.call(executors, Callable<Vector<Box>> { detectFaces(null, minFaceSize) })
        }


    }

    private fun detectFaces(bitmap: Bitmap?, minFaceSize: Int): Vector<Box>? {
        bitmap ?: return null

        val t_start = System.currentTimeMillis()
        //【1】PNet generate candidate boxes
        var boxes = PNet(bitmap, minFaceSize)
        square_limit(boxes, bitmap.width, bitmap.height)
        //【2】RNet
        boxes = RNet(bitmap, boxes)
        square_limit(boxes, bitmap.width, bitmap.height)
        //【3】ONet
        boxes = ONet(bitmap, boxes)
        //return
        lastProcessTime = System.currentTimeMillis() - t_start
        bitmap.recycle()
        return boxes
    }

    companion object {
        //MODEL PATH
        private const val MODEL_FILE = "file:///android_asset/mtcnn_freezed_model.pb"

        //tensor name
        private const val PNetInName = "pnet/input:0"
        private val PNetOutName =
                arrayOf("pnet/prob1:0", "pnet/conv4-2/BiasAdd:0")
        private const val RNetInName = "rnet/input:0"
        private val RNetOutName =
                arrayOf("rnet/prob1:0", "rnet/conv5-2/conv5-2:0")
        private const val ONetInName = "onet/input:0"
        private val ONetOutName = arrayOf(
                "onet/prob1:0",
                "onet/conv6-2/conv6-2:0",
                "onet/conv6-3/conv6-3:0"
        )
        private const val TAG = "MTCNN"
    }

    private fun flipDiagonal(data: FloatArray, h: Int, w: Int, stride: Int) {
        val tmp = FloatArray(w * h * stride)
        for (i in 0 until w * h * stride) tmp[i] = data[i]
        for (y in 0 until h)
            for (x in 0 until w) {
                for (z in 0 until stride)
                    data[(x * h + y) * stride + z] = tmp[(y * w + x) * stride + z]
            }
    }

    //src转为三维存放到dst中
    private fun expand(src: FloatArray, dst: Array<Array<FloatArray>>) {
        var idx = 0
        for (y in dst.indices)
            for (element in dst[0])
                for (c in dst[0][0].indices)
                    element[c] = src[idx++]

    }

    //dst=src[:,:,1]
    private fun expandProb(src: FloatArray, dst: Array<FloatArray>) {
        var idx = 0
        for (y in dst.indices)
            for (x in dst[0].indices)
                dst[y][x] = src[idx++ * 2 + 1]
    }

    //删除做了delete标记的box
    private fun updateBoxes(boxes: Vector<Box>): Vector<Box> {
        val b = Vector<Box>()
        for (i in boxes.indices)
            if (!boxes[i].deleted)
                b.addElement(boxes[i])
        return b
    }

    init {
        loadModel()
    }

}