package com.example.android_object_detection.data.detector

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.example.android_object_detection.domain.model.BoundingBox
import com.example.android_object_detection.domain.model.DetectionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TfliteObjectDetector @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ObjectDetector {

    private val lazyInterpreter = lazy { initInterpreter() }
    private val interpreter: Interpreter by lazyInterpreter

    private val labels: List<String> by lazy { loadLabels() }

    override fun detect(bitmap: Bitmap): List<DetectionResult> {
        val inputBuffer = preprocessBitmap(bitmap)

        val outputBoxes = Array(1) { Array(MAX_DETECTIONS) { FloatArray(4) } }
        val outputClasses = Array(1) { FloatArray(MAX_DETECTIONS) }
        val outputScores = Array(1) { FloatArray(MAX_DETECTIONS) }
        val numDetections = FloatArray(1)

        val outputs = mapOf(
            0 to outputBoxes,
            1 to outputClasses,
            2 to outputScores,
            3 to numDetections
        )

        val startMs = System.currentTimeMillis()
        interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputs)
        Log.d(TAG, "Inference took ${System.currentTimeMillis() - startMs}ms")

        return parseResults(
            boxes = outputBoxes[0],
            classes = outputClasses[0],
            scores = outputScores[0],
            detectionCount = numDetections[0].toInt().coerceIn(0, MAX_DETECTIONS),
            imageWidth = bitmap.width,
            imageHeight = bitmap.height
        )
    }

    private fun preprocessBitmap(source: Bitmap): ByteBuffer {
        val resized = source.scale(INPUT_SIZE, INPUT_SIZE)
        // 1 batch * height * width * 3 channels, UINT8
        val buffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        for (pixel in pixels) {
            buffer.put(((pixel shr 16) and 0xFF).toByte()) // R
            buffer.put(((pixel shr 8) and 0xFF).toByte())  // G
            buffer.put((pixel and 0xFF).toByte())           // B
        }
        buffer.rewind()
        return buffer
    }

    private fun parseResults(
        boxes: Array<FloatArray>,
        classes: FloatArray,
        scores: FloatArray,
        detectionCount: Int,
        imageWidth: Int,
        imageHeight: Int
    ): List<DetectionResult> {
        return (0 until detectionCount)
            .filter { i -> scores[i] >= CONFIDENCE_THRESHOLD }
            .map { i ->
                // Label map has background at index 0; model class output is 0-indexed to real classes
                val label = labels.getOrElse(classes[i].toInt() + LABEL_OFFSET) { UNKNOWN_LABEL }
                // EfficientDet boxes: normalized [ymin, xmin, ymax, xmax]
                val (yMin, xMin, yMax, xMax) = boxes[i]
                DetectionResult(
                    label = label,
                    confidence = scores[i],
                    boundingBox = BoundingBox(
                        left = xMin * imageWidth,
                        top = yMin * imageHeight,
                        right = xMax * imageWidth,
                        bottom = yMax * imageHeight
                    )
                )
            }
            .also { Log.d(TAG, "Detected ${it.size} objects above threshold") }
    }

    private fun initInterpreter(): Interpreter {
        return try {
            val modelBuffer = context.assets.openFd(MODEL_FILENAME).use { fd ->
                fd.createInputStream().channel.map(
                    java.nio.channels.FileChannel.MapMode.READ_ONLY,
                    fd.startOffset,
                    fd.declaredLength
                )
            }
            val options = Interpreter.Options().apply {
                numThreads = NUM_THREADS
            }
            Interpreter(modelBuffer, options).also {
                Log.d(TAG, "Model loaded: $MODEL_FILENAME")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to load model '$MODEL_FILENAME'. Add it to app/src/main/assets/", e)
            throw e
        }
    }

    private fun loadLabels(): List<String> {
        return try {
            context.assets.open(LABELS_FILENAME).bufferedReader().readLines()
                .also { Log.d(TAG, "Loaded ${it.size} labels") }
        } catch (e: IOException) {
            Log.w(TAG, "Labels file not found: $LABELS_FILENAME", e)
            emptyList()
        }
    }

    private companion object {
        private const val TAG = "TfliteObjectDetector"
        private const val MODEL_FILENAME = "efficientdet_lite0.tflite"
        private const val LABELS_FILENAME = "coco_labels.txt"

        private const val INPUT_SIZE = 300
        private const val MAX_DETECTIONS = 10
        private const val CONFIDENCE_THRESHOLD = 0.4f
        private const val NUM_THREADS = 4
        private const val LABEL_OFFSET = 1

        private const val UNKNOWN_LABEL = "unknown"
    }
}

private operator fun FloatArray.component1() = this[0]
private operator fun FloatArray.component2() = this[1]
private operator fun FloatArray.component3() = this[2]
private operator fun FloatArray.component4() = this[3]
