package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.stubs.ErrorCVBackend
import com.github.e13mort.codeview.stubs.StubCVBackend
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.exceptions.base.MockitoException
import java.lang.Exception
import java.nio.file.Paths

internal class LoggedBackendTest {
    @Mock
    lateinit var log: Log

    @BeforeEach
    internal fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    internal fun `regular backend handling leads to two log calls`() {
        StubCVBackend().withLogs(log).prepareTransformOperation(Paths.get("path")).test()
        verify(log, times(2)).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of transform observable doesn't lead to log calls`() {
        StubCVBackend().withLogs(log).prepareTransformOperation(Paths.get("path"))
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to different log calls`() {
        ErrorCVBackend().withLogs(log).prepareTransformOperation(Paths.get("path")).test()
        verify(log, times(1)).log(anyOrNull<String>())
        verify(log, times(1)).log(anyOrNull<Exception>())
    }

    @Test
    internal fun `regular backend handling with classes call leads to three log calls`() {
        StubCVBackend().withLogs(log)
            .prepareTransformOperation(Paths.get("path"))
            .doOnSuccess { it.classes() }
            .test()
        verify(log, times(3)).log(anyOrNull<String>())
    }
}