package com.example.android_object_detection.presentation.result

import androidx.compose.runtime.Immutable
import com.example.android_object_detection.domain.model.DetectionResult

sealed interface ResultUiState {
    data object Idle : ResultUiState
    data object Loading : ResultUiState

    @Immutable
    class Success(
        val originalUriString: String,
        val annotatedBytes: ByteArray,
        val detections: List<DetectionResult>
    ) : ResultUiState {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return originalUriString == other.originalUriString &&
                    annotatedBytes.contentEquals(other.annotatedBytes) &&
                    detections == other.detections
        }

        override fun hashCode(): Int {
            var result = originalUriString.hashCode()
            result = 31 * result + annotatedBytes.contentHashCode()
            result = 31 * result + detections.hashCode()
            return result
        }

        override fun toString(): String =
            "Success(originalUriString=$originalUriString, " +
                    "annotatedBytes=${annotatedBytes.size} bytes, " +
                    "detections=$detections)"
    }

    data class Error(val message: String) : ResultUiState
}