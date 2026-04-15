package com.example.android_object_detection.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.android_object_detection.presentation.home.homeEntry

@Composable
fun NavGraph(startDestination: NavKey = HomeNavKey) {
    val backStack = rememberNavBackStack(startDestination)
    val navigator = remember(backStack) { Navigator(backStack) }

    NavDisplay(
        backStack = backStack,
        onBack = navigator::goBack,
        entryProvider = entryProvider {
            homeEntry(navigator)
        }
    )
}
