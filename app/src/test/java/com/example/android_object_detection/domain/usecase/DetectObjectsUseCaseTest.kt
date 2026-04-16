package com.example.android_object_detection.domain.usecase

import com.example.android_object_detection.domain.model.BoundingBox
import com.example.android_object_detection.domain.model.DetectionResult
import com.example.android_object_detection.domain.repository.DetectionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DetectObjectsUseCaseTest {

    private lateinit var repository: DetectionRepository
    private lateinit var useCase: DetectObjectsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = DetectObjectsUseCase(repository)
    }

    @Test
    fun `invoke returns success with detections from repository`() = runTest {
        val expected = listOf(
            DetectionResult(
                label = "person",
                confidence = 0.88f,
                boundingBox = BoundingBox(0f, 0f, 100f, 200f)
            )
        )
        val imageBytes = byteArrayOf(1, 2, 3)
        coEvery { repository.detect(imageBytes) } returns expected

        val result = useCase(imageBytes)

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
        coVerify(exactly = 1) { repository.detect(imageBytes) }
    }

    @Test
    fun `invoke returns success with empty list when no objects detected`() = runTest {
        val imageBytes = byteArrayOf(1, 2, 3)
        coEvery { repository.detect(imageBytes) } returns emptyList()

        val result = useCase(imageBytes)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `invoke wraps repository exception in failure Result`() = runTest {
        val error = RuntimeException("Model inference failed")
        val imageBytes = byteArrayOf(1, 2, 3)
        coEvery { repository.detect(imageBytes) } throws error

        val result = useCase(imageBytes)

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }

    @Test
    fun `invoke wraps IllegalStateException for corrupt image`() = runTest {
        val imageBytes = byteArrayOf()
        coEvery { repository.detect(imageBytes) } throws IllegalStateException("Could not load image")

        val result = useCase(imageBytes)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }
}
