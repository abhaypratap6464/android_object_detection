package com.example.android_object_detection.presentation.result

import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.android_object_detection.presentation.navigation.Navigator
import com.example.android_object_detection.presentation.navigation.ResultNavKey

fun EntryProviderScope<NavKey>.resultEntry(navigator: Navigator) {
    entry<ResultNavKey> { backStackEntry ->
        val imageUriString = backStackEntry.imageUriString
        imageUriString.toUri()

        ResultScreen(
            uiState = ResultUiState.Idle,
            onGoToMain = { navigator.goBack() }
        )
    }
}