package com.example.android_object_detection.data.repository

import android.graphics.BitmapFactory
import com.example.android_object_detection.core.util.ErrorMessages
import com.example.android_object_detection.domain.model.BoundingBox
import com.example.android_object_detection.domain.model.DetectionResult
import com.example.android_object_detection.domain.repository.DetectionRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class DetectionRepositoryImpl @Inject constructor() : DetectionRepository {

    private val detector: ObjectDetector by lazy {
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
            .let { ObjectDetection.getClient(it) }
    }

    override suspend fun detect(imageBytes: ByteArray): List<DetectionResult> =
        suspendCancellableCoroutine { continuation ->
            val bitmap = BitmapFactory
                .decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: run {
                    continuation.resumeWithException(
                        IllegalStateException(ErrorMessages.COULD_NOT_LOAD_IMAGE)
                    )
                    return@suspendCancellableCoroutine
                }

            detector
                .process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener { objects ->
                    val results = objects
                        .filter { it.labels.isNotEmpty() }
                        .map { obj ->
                            val topLabel = obj.labels.maxByOrNull { it.confidence }
                            DetectionResult(
                                label = topLabel?.text ?: ErrorMessages.UNKNOWN_LABEL,
                                confidence = topLabel?.confidence ?: 0f,
                                boundingBox = BoundingBox(
                                    left = obj.boundingBox.left.toFloat(),
                                    top = obj.boundingBox.top.toFloat(),
                                    right = obj.boundingBox.right.toFloat(),
                                    bottom = obj.boundingBox.bottom.toFloat()
                                )
                            )
                        }
                    continuation.resume(results)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }

            // cancel ML Kit task if coroutine is cancelled
            continuation.invokeOnCancellation {
                detector.close()
            }
        }
}