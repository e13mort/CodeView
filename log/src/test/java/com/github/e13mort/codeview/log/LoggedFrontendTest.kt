package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.stubs.ErrorCVFrontend
import com.github.e13mort.codeview.stubs.StubCVClasses
import com.github.e13mort.codeview.stubs.StubCVFrontend
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

internal class LoggedFrontendTest {
    @Test
    internal fun `regular frontend handling leads to two log calls`() {
        val log = mock<Log>()
        StubCVFrontend().withLogs(log).generate(StubCVClasses()).test()
        verify(log, times(2)).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of transform observable doesn't lead to log calls`() {
        val log = mock<Log>()
        StubCVFrontend().withLogs(log).generate(StubCVClasses())
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to different log calls`() {
        val log = mock<Log>()
        ErrorCVFrontend().withLogs(log).generate(StubCVClasses()).test()
        verify(log, times(1)).log(anyOrNull<String>())
        verify(log, times(1)).log(anyOrNull<Exception>())
    }
}