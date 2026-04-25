package com.example.android_object_detection.data.repository

import android.graphics.BitmapFactory
import com.example.android_object_detection.core.util.ErrorMessages
import com.example.android_object_detection.data.detector.ObjectDetector
import com.example.android_object_detection.domain.model.DetectionResult
import com.example.android_object_detection.domain.repository.DetectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetectionRepositoryImpl @Inject constructor(
    private val objectDetector: ObjectDetector
) : DetectionRepository {

    override suspend fun detect(imageBytes: ByteArray): List<DetectionResult> =
        withContext(Dispatchers.Default) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: throw IllegalStateException(ErrorMessages.COULD_NOT_LOAD_IMAGE)
            objectDetector.detect(bitmap)
        }
}
