package com.example.android_object_detection.domain.model


import androidx.compose.runtime.Immutable

@Immutable
data class DetectionResult(
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox
)