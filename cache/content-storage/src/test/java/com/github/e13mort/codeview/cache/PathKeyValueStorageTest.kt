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

package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.asContent
import com.github.e13mort.codeview.asString
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PathKeyValueStorageTest {

    private val fs = Jimfs.newFileSystem()

    private val storage = PathKeyValueStorage(
        fs.getPath("data"),
        UUIDCacheName(),
        PathRegistry(fs.getPath("reg.json"))
    )

    @Test
    internal fun `put single content returns path to the target file`() {
        storage.putSingleItem("key", "hello".asContent())
        assertEquals("hello", storage.searchSingleItem("key")!!.asString())
    }

    @Test
    internal fun `search for a single not existing value returns null result`() {
        assertNull(storage.searchSingleItem("key"))
    }

    @Test
    internal fun `search for a single existing value returns not null result`() {
        storage.putSingleItem("key", "hello".asContent())
        assertNotNull(storage.searchSingleItem("key"))
    }

    @Test
    internal fun `search for a single existing value returns a valid result`() {
        storage.putSingleItem("key", "hello".asContent())
        storage.searchSingleItem("key")!!.apply {
            assertEquals("hello", asString())
        }
    }

    @Test
    internal fun `removing single item leads to empty result`() {
        storage.putSingleItem("test", "hello".asContent())
        storage.remove("test")
        assertNull(storage.searchSingleItem("test"))
    }

    @Test
    internal fun `removing single item leads to actual files removal`() {
        storage.putSingleItem("test", "hello".asContent())
        storage.remove("test")
        assertEquals(0L, fs.getPath("data").internalDirsCount())
    }
}