package com.example.android_object_detection.domain.usecase

import com.example.android_object_detection.domain.repository.ImageRepository
import javax.inject.Inject

class LoadImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(uriString: String): Result<ByteArray> =
        runCatching { imageRepository.loadImageBytes(uriString) }
}