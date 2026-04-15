package com.example.android_object_detection.domain.repository

import com.example.android_object_detection.domain.model.DetectionResult

interface DetectionRepository {
    suspend fun detect(imageBytes: ByteArray): List<DetectionResult>
}