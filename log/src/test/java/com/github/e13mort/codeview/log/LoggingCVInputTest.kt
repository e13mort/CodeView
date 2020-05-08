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
