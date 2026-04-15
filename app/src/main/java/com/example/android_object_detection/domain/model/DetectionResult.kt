package com.example.android_object_detection.domain.model

import android.graphics.RectF

data class DetectionResult(
    val label: String,
    val confidence: Float,
    val boundingBox: RectF
)