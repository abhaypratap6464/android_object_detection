package com.example.android_object_detection.presentation.navigation

import androidx.navigation3.runtime.NavKey

class Navigator(private val backStack: MutableList<NavKey>) {

    fun navigateToResult(imageUriString: String) {
        backStack.add(ResultNavKey(imageUriString))
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
