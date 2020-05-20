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

internal abstract class KeyValueStorageTest<T: KeyValueStorage> {

    protected val fs = Jimfs.newFileSystem()!!

    private val storage = PathKeyValueStorage(
        fs.getPath("data"),
        UUIDCacheName(),
        PathRegistry(fs.getPath("reg.json"))
    )

    @Test
    fun `put single content returns path to the target file`() {
        val storage = storage()
        storage.put("key", "hello".asContent())
        assertEquals("hello", storage.search("key")!!.asString())
    }

    @Test
    fun `search for a single not existing value returns null result`() {
        assertNull(storage().search("key"))
    }

    @Test
    fun `search for a single existing value returns not null result`() {
        val storage = storage()
        storage.put("key", "hello".asContent())
        assertNotNull(storage.search("key"))
    }

    @Test
    fun `search for a single existing value returns a valid result`() {
        val storage = storage()
        storage.put("key", "hello".asContent())
        storage.search("key")!!.apply {
            assertEquals("hello", asString())
        }
    }

    @Test
    fun `removing single item leads to empty result`() {
        val storage = storage()
        storage.put("test", "hello".asContent())
        storage.remove("test")
        assertNull(storage.search("test"))
    }

    @Test
    fun `removing single item leads to actual files removal`() {
        val storage = storage()
        storage.put("test", "hello".asContent())
        storage.remove("test")
        assertEquals(0L, fs.getPath("data").internalDirsCount())
    }

    abstract fun storage() : T
}

internal class PathKeyValueStorageTest : KeyValueStorageTest<PathKeyValueStorage>() {
    override fun storage(): PathKeyValueStorage {
        return PathKeyValueStorage(
            fs.getPath("data"),
            UUIDCacheName(),
            PathRegistry(fs.getPath("reg.json"))
        )
    }
}

internal class PlainFilesKeyValueStorageTest : KeyValueStorageTest<PlainFilesKeyValueStorage>() {
    override fun storage(): PlainFilesKeyValueStorage {
        return PlainFilesKeyValueStorage(fs.getPath("data"))
    }
}