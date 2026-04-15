package com.example.android_object_detection.domain.repository

interface ImageRepository {
    suspend fun loadImageBytes(uriString: String): ByteArray
}