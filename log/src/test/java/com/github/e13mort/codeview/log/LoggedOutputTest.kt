package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.stubs.ErrorTransformOperation
import com.github.e13mort.codeview.stubs.StubFrontendTransformOperation
import com.github.e13mort.codeview.stubs.StubOutput
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

internal class LoggedOutputTest {
    private val log = mock<Log>()

    @Test
    internal fun `regular output save leads to one log calls`() {
        StubOutput("test").withLogs(log).save(StubFrontendTransformOperation()).test()
        verify(log).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of save observable doesn't lead to log calls`() {
        StubOutput("test").withLogs(log).save(StubFrontendTransformOperation())
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to error log call`() {
        StubOutput("test").withLogs(log).save(ErrorTransformOperation()).test()
        verify(log, times(1)).log(anyOrNull<Exception>())
    }
}