package com.example.android_object_detection.presentation.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onPhotoPickerStarted() {
        _uiState.value = _uiState.value.copy(isPickingPhoto = true)
    }

    fun onPhotoPickerFinished() {
        _uiState.value = _uiState.value.copy(isPickingPhoto = false)
    }

    fun onImageSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }
}