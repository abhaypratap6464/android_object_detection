package com.example.android_object_detection.presentation.result

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.android_object_detection.R
import com.example.android_object_detection.presentation.components.PrimaryButton

@Composable
fun ResultScreen(
    uiState: ResultUiState,
    onGoToMain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is ResultUiState.Idle -> Unit
            is ResultUiState.Loading -> LoadingContent()
            is ResultUiState.Success -> SuccessContent(uiState, onGoToMain)
            is ResultUiState.Error -> ErrorContent(uiState.message, onGoToMain)
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.detecting_objects),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: ResultUiState.Success,
    onGoToMain: () -> Unit
) {
    val annotatedImageBitmap = remember(uiState.annotatedBytes) {
        BitmapFactory.decodeByteArray(uiState.annotatedBytes, 0, uiState.annotatedBytes.size)
            ?.asImageBitmap()
    }

    Text(
        text = stringResource(R.string.original_image),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))

    AsyncImage(
        model = uiState.originalUriString,
        contentDescription = stringResource(R.string.original_image),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentScale = ContentScale.Fit
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(R.string.object_detection_image),
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))

    if (annotatedImageBitmap != null) {
        Image(
            bitmap = annotatedImageBitmap,
            contentDescription = stringResource(R.string.object_detection_image),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )
    } else {
        Text(
            text = stringResource(R.string.error_detection_failed),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    PrimaryButton(
        text = stringResource(R.string.go_back),
        onClick = onGoToMain,
        modifier = Modifier.padding(horizontal = 48.dp)
    )
}

@Composable
private fun ErrorContent(
    message: String,
    onGoToMain: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = stringResource(R.string.go_back),
            onClick = onGoToMain,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}


@Preview(showBackground = true, name = "Loading")
@Composable
fun ResultScreenLoadingPreview() {
    MaterialTheme {
        ResultScreen(uiState = ResultUiState.Loading, onGoToMain = {})
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
fun ResultScreenSuccessPreview() {
    MaterialTheme {
        ResultScreen(
            uiState = ResultUiState.Success(
                originalUriString = "https://picsum.photos/id/1015/800/600",
                annotatedBytes = ByteArray(0),
                detections = emptyList()
            ),
            onGoToMain = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
fun ResultScreenErrorPreview() {
    MaterialTheme {
        ResultScreen(
            uiState = ResultUiState.Error("Could not load image"),
            onGoToMain = {}
        )
    }
}