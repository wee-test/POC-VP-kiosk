package wee.digital.camera.detector

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerLocalModel
import com.google.mlkit.vision.label.automl.AutoMLImageLabelerOptions

class ModelFilter(fileName: String) {

    private var imageLabeler: ImageLabeler? = null

    private var isChecking: Boolean = false

    init {
        try {
            val model = AutoMLImageLabelerLocalModel
                    .Builder()
                    .setAssetFilePath(fileName)
                    .build()
            val options = AutoMLImageLabelerOptions.Builder(model)
                    .setConfidenceThreshold(0.5f)
                    .build()
            imageLabeler = ImageLabeling.getClient(options)
        } catch (e: Exception) {
        }
    }

    @Synchronized
    fun processImage(bitmap: Bitmap, onResult: (String?, Float) -> Unit) {
        if (isChecking) {
            return
        }
        isChecking = true
        try {
            val image = InputImage.fromBitmap(bitmap,0)
            imageLabeler?.process(image)
                    ?.addOnSuccessListener {
                        val label = it.firstOrNull()
                        onResult(label?.text, label?.confidence ?: 100f)
                        isChecking = false
                    }
                    ?.addOnFailureListener {
                        onResult(null, 100f)
                        isChecking = false
                    }

        } catch (e: Exception) {
            isChecking = false
        }
    }


    fun destroy() {
        imageLabeler?.close()
    }

}
