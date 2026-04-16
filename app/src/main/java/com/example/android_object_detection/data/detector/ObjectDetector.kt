package com.example.android_object_detection.data.detector

import android.graphics.Bitmap
import com.example.android_object_detection.domain.model.DetectionResult

interface ObjectDetector {
    fun detect(bitmap: Bitmap): List<DetectionResult>
}
