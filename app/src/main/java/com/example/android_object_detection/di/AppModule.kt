package com.example.android_object_detection.di

import android.content.ContentResolver
import android.content.Context
import com.example.android_object_detection.data.detector.ObjectDetector
import com.example.android_object_detection.data.detector.TfliteObjectDetector
import com.example.android_object_detection.data.repository.BitmapRendererImpl
import com.example.android_object_detection.data.repository.DetectionRepositoryImpl
import com.example.android_object_detection.data.repository.ImageRepositoryImpl
import com.example.android_object_detection.domain.repository.BitmapRenderer
import com.example.android_object_detection.domain.repository.DetectionRepository
import com.example.android_object_detection.domain.repository.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindObjectDetector(
        impl: TfliteObjectDetector
    ): ObjectDetector

    @Binds
    @Singleton
    abstract fun bindDetectionRepository(
        impl: DetectionRepositoryImpl
    ): DetectionRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        impl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    @Singleton
    abstract fun bindBitmapRenderer(
        impl: BitmapRendererImpl
    ): BitmapRenderer

    companion object {
        @Provides
        fun provideContentResolver(
            @ApplicationContext context: Context
        ): ContentResolver = context.contentResolver
    }
}
