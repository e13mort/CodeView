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

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

internal class TaggedLogTest {
    @Test
    internal fun `tag with delimiter added at the beginning of msg`() {
        mock<Log>().let {
            it.withTag("tag").log("msg")
            verify(it).log("tag: msg")
        }
    }

    @Test
    internal fun `throwable is handled`() {
        mock<Log>().let {
            val throwable = Exception()
            it.withTag("tag").log(throwable)
            verify(it).log(eq(throwable))
        }
    }
}