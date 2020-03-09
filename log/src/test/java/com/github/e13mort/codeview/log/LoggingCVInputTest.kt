package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.stubs.ErrorCVInput
import com.github.e13mort.codeview.stubs.StubCVInput
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.jupiter.api.Test
import java.nio.file.Path

internal class LoggingCVInputTest {
    private val log = mock<Log>()

    @Test
    internal fun `regular input handling leads to one log call`() {
        wrapAndCall(StubCVInput()).test()
        verify(log).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of single observable doesn't lead to log event`() {
        wrapAndCall(StubCVInput())
        verify(log, never()).log(anyOrNull<String>())
    }

    @Test
    internal fun `handling with error leads to error call`() {
        wrapAndCall(ErrorCVInput()).test()
        verify(log).log(anyOrNull<Throwable>())
    }

    private fun wrapAndCall(source: CVInput): Single<Path> {
        return source.withLogs(log).prepare("input").flatMap { it.transform() }
    }
}
