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