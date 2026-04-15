package com.example.android_object_detection.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeNavKey : NavKey

@Serializable
data class ResultNavKey(val imageUriString: String) : NavKey