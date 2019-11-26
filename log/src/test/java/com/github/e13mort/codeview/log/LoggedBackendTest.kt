package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.stubs.ErrorCVBackend
import com.github.e13mort.codeview.stubs.StubCVBackend
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import java.lang.Exception
import java.nio.file.Paths

internal class LoggedBackendTest {
    @Test
    internal fun `regular backend handling leads to two log calls`() {
        val log = mock<Log>()
        StubCVBackend().withLogs(log).transformSourcesToCVClasses(Paths.get("path")).test()
        verify(log, times(2)).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of transform observable doesn't lead to log calls`() {
        val log = mock<Log>()
        StubCVBackend().withLogs(log).transformSourcesToCVClasses(Paths.get("path"))
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to different log calls`() {
        val log = mock<Log>()
        ErrorCVBackend().withLogs(log).transformSourcesToCVClasses(Paths.get("path")).test()
        verify(log, times(1)).log(anyOrNull<String>())
        verify(log, times(1)).log(anyOrNull<Exception>())
    }
}