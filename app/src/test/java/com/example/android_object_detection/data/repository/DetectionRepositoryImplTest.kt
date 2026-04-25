package com.example.android_object_detection.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.android_object_detection.core.util.ErrorMessages
import com.example.android_object_detection.data.detector.FakeObjectDetector
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetectionRepositoryImplTest {

    private lateinit var fakeDetector: FakeObjectDetector
    private lateinit var repository: DetectionRepositoryImpl

    @Before
    fun setup() {
        fakeDetector = FakeObjectDetector()
        repository = DetectionRepositoryImpl(fakeDetector)
    }

    @Test
    fun `detect returns mapped results from ObjectDetector`() = runTest {
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns mockBitmap

        val results = repository.detect(byteArrayOf(1, 2, 3))

        assertEquals(FakeObjectDetector.DEFAULT_RESULTS, results)
        assertEquals(1, fakeDetector.detectCallCount)
    }

    @Test(expected = IllegalStateException::class)
    fun `detect throws when bitmap decoding fails`() = runTest {
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns null

        repository.detect(byteArrayOf())
    }

    @Test
    fun `detect error message matches expected constant when bitmap is null`() = runTest {
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns null

        val exception = runCatching { repository.detect(byteArrayOf()) }.exceptionOrNull()

        assertTrue(exception is IllegalStateException)
        assertEquals(ErrorMessages.COULD_NOT_LOAD_IMAGE, exception?.message)
    }

    @Test(expected = RuntimeException::class)
    fun `detect propagates exception from ObjectDetector`() = runTest {
        val throwingDetector = FakeObjectDetector(shouldThrow = true)
        val repo = DetectionRepositoryImpl(throwingDetector)
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns mockBitmap

        repo.detect(byteArrayOf(1, 2, 3))
    }

    @Test
    fun `detect returns empty list when detector finds nothing`() = runTest {
        val emptyDetector = FakeObjectDetector(results = emptyList())
        val repo = DetectionRepositoryImpl(emptyDetector)
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns mockBitmap

        val results = repo.detect(byteArrayOf(1, 2, 3))

        assertTrue(results.isEmpty())
    }
}
