package com.example.android_object_detection.domain.usecase

import com.example.android_object_detection.domain.model.DetectionResult
import com.example.android_object_detection.domain.repository.DetectionRepository
import javax.inject.Inject

class DetectObjectsUseCase @Inject constructor(
    private val repository: DetectionRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray
    ): Result<List<DetectionResult>> =
        runCatching { repository.detect(imageBytes) }
}