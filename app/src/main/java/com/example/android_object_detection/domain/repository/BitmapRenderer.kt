package com.example.android_object_detection.domain.repository

import com.example.android_object_detection.domain.model.DetectionResult

interface BitmapRenderer {
    suspend fun drawBoundingBoxes(
        imageBytes: ByteArray,
        detections: List<DetectionResult>
    ): ByteArray
}