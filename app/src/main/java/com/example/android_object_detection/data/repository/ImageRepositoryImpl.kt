package com.example.android_object_detection.data.repository

import android.content.ContentResolver
import androidx.core.net.toUri
import com.example.android_object_detection.core.util.ErrorMessages
import com.example.android_object_detection.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ImageRepository {

    override suspend fun loadImageBytes(uriString: String): ByteArray =
        contentResolver
            .openInputStream(uriString.toUri())
            ?.use { it.readBytes() }
            ?: throw IllegalStateException(ErrorMessages.COULD_NOT_LOAD_IMAGE)
}