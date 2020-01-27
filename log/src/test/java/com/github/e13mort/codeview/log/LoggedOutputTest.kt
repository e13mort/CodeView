package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.stubs.ErrorOutput
import com.github.e13mort.codeview.stubs.StubFrontendTransformOperation
import com.github.e13mort.codeview.stubs.StubOutput
import com.github.e13mort.codeview.stubs.StubStoreObject
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

internal class LoggedOutputTest {
    @Test
    internal fun `regular output save leads to two log calls`() {
        val log = mock<Log>()
        StubOutput("test").withLogs(log).save(StubFrontendTransformOperation()).test()
        verify(log, times(2)).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of save observable doesn't lead to log calls`() {
        val log = mock<Log>()
        StubOutput("test").withLogs(log).save(StubFrontendTransformOperation())
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to different log calls`() {
        val log = mock<Log>()
        ErrorOutput<Any>().withLogs(log).save(StubFrontendTransformOperation()).test()
        verify(log, times(1)).log(anyOrNull<String>())
        verify(log, times(1)).log(anyOrNull<Exception>())
    }
}