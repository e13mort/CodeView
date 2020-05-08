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

import com.github.e13mort.codeview.cache.ContentStorage
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test

internal class LoggedContentStorageTest {

    private val contentStorage : ContentStorage<Any> = mock()

    private val log : Log = mock()

    @Test
    internal fun `existing item leads to regular log call`() {
        whenever(contentStorage.search(any())).thenReturn(mock())
        LoggedContentStorage(contentStorage, log).search("key")
        verify(log).log(eq("item found for key key"))
    }

    @Test
    internal fun `non existing item leads to error log call`() {
        whenever(contentStorage.search(any())).thenReturn(null)
        LoggedContentStorage(contentStorage, log).search("key")
        verify(log).log(eq("item isn't found for key key"))
    }
}