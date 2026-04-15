package com.example.android_object_detection.presentation.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.android_object_detection.R
import com.example.android_object_detection.presentation.components.PrimaryButton

@Composable
fun HomeScreen(
    onImagePicked: (Uri) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onPhotoPickerFinished()
        uri?.let {
            viewModel.onImageSelected(it)
            onImagePicked(it)
        }
    }

    HomeContent(
        uiState = uiState,
        onPickPhotoClick = {
            if (!uiState.isPickingPhoto) {
                viewModel.onPhotoPickerStarted()
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onPickPhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PrimaryButton(
            text = stringResource(R.string.open_photos),
            onClick = onPickPhotoClick,
            enabled = !uiState.isPickingPhoto,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen - Idle")
@Composable
fun HomeScreenIdlePreview() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(isPickingPhoto = false),
            onPickPhotoClick = {}
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen - Picking Photo")
@Composable
fun HomeScreenPickingPreview() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(isPickingPhoto = true),
            onPickPhotoClick = {}
        )
    }
}