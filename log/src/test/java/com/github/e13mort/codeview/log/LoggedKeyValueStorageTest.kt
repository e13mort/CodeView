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

import com.github.e13mort.codeview.asContent
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LoggedKeyValueStorageTest {

    private val log : Log = mock()
    private val source : KeyValueStorage = mock()
    private val target = source.withLogs(log)

    @BeforeEach
    internal fun setUp() {
        whenever(source.search(any())).thenReturn(mock())
    }

    @Test
    fun putSingleItem() {
        target.put("key", "value".asContent())
        verify(log).log(any<String>())
        verify(source).put(eq("key"), any())
    }

    @Test
    fun searchSingleItem() {
        target.search("key")
        verify(log).log(any<String>())
        verify(source).search("key")
    }

    @Test
    fun remove() {
        target.remove("key")
        verify(log).log(any<String>())
        verify(source).remove("key")
    }
}