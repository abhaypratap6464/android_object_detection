package com.example.android_object_detection.presentation.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.android_object_detection.presentation.navigation.HomeNavKey
import com.example.android_object_detection.presentation.navigation.Navigator

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeScreen(
            onImagePicked = { uri ->
                navigator.navigateToResult(
                    imageUriString = uri.toString()
                )
            }
        )
    }
}