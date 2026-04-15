package com.example.android_object_detection.presentation.home

import android.net.Uri

data class HomeUiState(
    val isPickingPhoto: Boolean = false,
    val selectedImageUri: Uri? = null
)