package com.example.android_object_detection.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_object_detection.core.util.ErrorMessages
import com.example.android_object_detection.core.util.Result
import com.example.android_object_detection.core.util.asResult
import com.example.android_object_detection.domain.usecase.DetectObjectsUseCase
import com.example.android_object_detection.domain.usecase.DrawBoundingBoxesUseCase
import com.example.android_object_detection.domain.usecase.LoadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val loadImageUseCase: LoadImageUseCase,
    private val detectObjectsUseCase: DetectObjectsUseCase,
    private val drawBoundingBoxesUseCase: DrawBoundingBoxesUseCase
) : ViewModel() {

    private val imageUriString = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ResultUiState> = imageUriString
        .flatMapLatest { uriString ->
            if (uriString == null) {
                return@flatMapLatest flow { emit(ResultUiState.Idle) }
            }
            flow {
                val imageBytes = loadImageUseCase(uriString).getOrThrow()
                val detections = detectObjectsUseCase(imageBytes).getOrThrow()
                val annotatedBytes = drawBoundingBoxesUseCase(imageBytes, detections).getOrThrow()
                emit(
                    ResultUiState.Success(
                        originalUriString = uriString,
                        annotatedBytes = annotatedBytes,
                        detections = detections
                    )
                )
            }
        }
        .asResult()
        .map { result ->
            when (result) {
                is Result.Loading -> ResultUiState.Loading
                is Result.Success -> result.data        // ← already ResultUiState.Success
                is Result.Error -> ResultUiState.Error(
                    message = result.exception?.message
                        ?: ErrorMessages.DETECTION_FAILED
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ResultUiState.Idle
        )

    fun processImage(uriString: String) {
        imageUriString.value = uriString
    }
}