package com.example.android_object_detection.domain.usecase

import com.example.android_object_detection.domain.model.DetectionResult
import com.example.android_object_detection.domain.repository.BitmapRenderer
import javax.inject.Inject

class DrawBoundingBoxesUseCase @Inject constructor(
    private val renderer: BitmapRenderer
) {

    suspend operator fun invoke(
        imageBytes: ByteArray,
        detections: List<DetectionResult>
    ): Result<ByteArray> =
        runCatching { renderer.drawBoundingBoxes(imageBytes, detections) }
}