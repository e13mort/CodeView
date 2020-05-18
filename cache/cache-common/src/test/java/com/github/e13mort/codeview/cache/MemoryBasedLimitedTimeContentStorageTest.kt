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
import com.github.e13mort.codeview.cache.fake.FkTime
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MemoryBasedLimitedTimeContentStorageTest {
    private val time = FkTime()
    private val target = PathBasedStorage(
        Jimfs.newFileSystem().getPath("."),
        UUIDCacheName(),
        PathRegistry(Jimfs.newFileSystem().getPath(".").resolve("registry.json"))
    ).withTimeLimit(10, time)

    @Test
    internal fun `existing item is returned if ttl isn't expired`() {
        target.putSingleItem("key", "value".asContent())
        time.advanceSeconds(1)
        assertNotNull(target.searchSingleItem("key"))
    }

    @Test
    internal fun `existing item's ttl updated`() {
        target.putSingleItem("key", "value".asContent())
        time.advanceSeconds(5)
        target.putSingleItem("key", "value2".asContent())
        time.advanceSeconds(7)
        assertNotNull(target.searchSingleItem("key"))
    }

    @Test
    internal fun `null is returned if ttl is expired`() {
        target.putSingleItem("key", "value".asContent())
        time.advanceSeconds(11)
        assertNull(target.searchSingleItem("key"))
    }

    @Test
    internal fun `returned item's content is valid`() {
        target.putSingleItem("key", "value".asContent())
        time.advanceSeconds(1)
        assertEquals("value", target.searchSingleItem("key")!!.asString())
    }

    @Test
    internal fun `returned updated item's content is valid`() {
        target.putSingleItem("key", "value".asContent())
        time.advanceSeconds(5)
        target.putSingleItem("key", "value2".asContent())
        assertEquals("value2", target.searchSingleItem("key")!!.asString())
    }

    @Test
    internal fun `remove method is unsupported`() {
        assertThrows<UnsupportedOperationException> {
            target.remove("key")
        }
    }
}