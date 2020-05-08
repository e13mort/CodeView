/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

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