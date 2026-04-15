package com.example.android_object_detection.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.android_object_detection.core.util.ErrorMessages
import com.example.android_object_detection.domain.model.DetectionResult
import com.example.android_object_detection.domain.repository.BitmapRenderer
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class BitmapRendererImpl @Inject constructor() : BitmapRenderer {

    private val boxColors = listOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN
    )

    override suspend fun drawBoundingBoxes(
        imageBytes: ByteArray,
        detections: List<DetectionResult>
    ): ByteArray {
        val source = BitmapFactory
            .decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IllegalStateException(ErrorMessages.COULD_NOT_LOAD_IMAGE)

        val output = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)

        val boxPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        val textPaint = Paint().apply {
            textSize = 36f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val backgroundPaint = Paint().apply {
            color = Color.argb(160, 0, 0, 0)
        }

        detections.forEachIndexed { index, detection ->
            val color = boxColors[index % boxColors.size]
            boxPaint.color = color
            textPaint.color = color

            val box = detection.boundingBox
            canvas.drawRect(
                box.left, box.top,
                box.right, box.bottom,
                boxPaint
            )

            val label = buildLabel(detection)
            val textWidth = textPaint.measureText(label)

            // label background
            canvas.drawRect(
                box.left,
                box.top - 44f,
                box.left + textWidth + 8f,
                box.top,
                backgroundPaint
            )
            // label text
            canvas.drawText(
                label,
                box.left + 4f,
                box.top - 10f,
                textPaint
            )
        }

        return ByteArrayOutputStream().use { stream ->
            output.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
    }

    private fun buildLabel(detection: DetectionResult): String =
        "${detection.label} ${"%.0f".format(detection.confidence * 100)}%"
}