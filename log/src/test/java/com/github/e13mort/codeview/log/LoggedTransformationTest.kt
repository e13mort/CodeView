package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.ProxyCVTransformation
import com.github.e13mort.codeview.stubs.ErrorTransformOperation
import com.github.e13mort.codeview.stubs.ErrorTransformation
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import org.junit.jupiter.api.Test

internal class LoggedTransformationTest {
    private val log = mock<Log>()

    @Test
    internal fun `transform operation emission does not call logger`() {
        ProxyCVTransformation<String>().withLogs(log).prepare("test".asTransformOperation()).test()
        verify(log, never()).log(anyOrNull<String>())
    }

    @Test
    internal fun `transform operation running calls logger one time`() {
        val log = mock<Log>()
        ProxyCVTransformation<String>().withLogs(log)
            .prepare("test".asTransformOperation())
            .doOnSuccess { it.transform().test() }
            .test()
        verify(log).log(anyOrNull<String>())
    }

    @Test
    internal fun `creation of transform observable doesn't lead to log calls`() {
        ProxyCVTransformation<String>().withLogs(log).prepare("test".asTransformOperation())
        verify(log, never()).log(anyOrNull<String>())
    }

    @Test
    internal fun `error during transformation chain lead to error log`() {
        ErrorTransformation<CVTransformation.TransformOperation<String>, String>()
            .withLogs(log).prepare("test".asTransformOperation()).test()
        verify(log, times(1)).log(anyOrNull<Exception>())
        verify(log, times(0)).log(anyOrNull<String>())
    }

    @Test
    internal fun `error during operation chain lead to error log`() {
        ProxyCVTransformation<String>().withLogs(log).prepare(ErrorTransformOperation())
            .doOnSuccess { it.transform().test() }
            .test()
        verify(log, times(1)).log(anyOrNull<Exception>())
        verify(log, times(0)).log(anyOrNull<String>())
    }
}

private fun String.asTransformOperation(): CVTransformation.TransformOperation<String> {
    val source = this
    return object : CVTransformation.TransformOperation<String> {
        override fun description(): String {
            return source
        }

        override fun transform(): Single<String> {
            return Single.just(source)
        }

    }
}