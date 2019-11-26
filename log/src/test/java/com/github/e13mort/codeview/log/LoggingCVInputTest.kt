package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.stubs.ErrorCVInput
import com.github.e13mort.codeview.stubs.StubCVInput
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

internal class LoggingCVInputTest {
    private lateinit var log : Log

    @BeforeEach
    internal fun setUp() {
        log = mock()
    }

    @Test
    internal fun `regular input handling leads to two log calls`() {
        wrapAndCall(StubCVInput()).test()
        verify(log, times(2)).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of single observable doesn't lead to log event`() {
        wrapAndCall(StubCVInput())
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to different log calls`() {
        wrapAndCall(ErrorCVInput()).test()
        verify(log, times(1)).log(anyOrNull<String>())
        verify(log, times(1)).log(anyOrNull<Throwable>())
    }

    private fun wrapAndCall(source: CVInput): Single<Path> {
        return source.withLogs(log).handleInput("input")
    }
}
