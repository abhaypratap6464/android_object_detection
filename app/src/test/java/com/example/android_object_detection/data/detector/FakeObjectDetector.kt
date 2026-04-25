package com.example.android_object_detection.data.detector

import android.graphics.Bitmap
import com.example.android_object_detection.domain.model.BoundingBox
import com.example.android_object_detection.domain.model.DetectionResult

internal class FakeObjectDetector(
    private val results: List<DetectionResult> = DEFAULT_RESULTS,
    private val shouldThrow: Boolean = false
) : ObjectDetector {

    var detectCallCount = 0
        private set

    override fun detect(bitmap: Bitmap): List<DetectionResult> {
        detectCallCount++
        if (shouldThrow) throw RuntimeException("Simulated inference failure")
        return results
    }

    companion object {
        val DEFAULT_RESULTS = listOf(
            DetectionResult(
                label = "cat",
                confidence = 0.92f,
                boundingBox = BoundingBox(left = 10f, top = 20f, right = 200f, bottom = 300f)
            ),
            DetectionResult(
                label = "dog",
                confidence = 0.75f,
                boundingBox = BoundingBox(left = 50f, top = 60f, right = 250f, bottom = 350f)
            )
        )
    }
}
