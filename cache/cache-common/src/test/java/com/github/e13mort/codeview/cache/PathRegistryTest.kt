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

import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PathRegistryTest {

    private val root = Jimfs.newFileSystem().getPath(".").resolve("registry.json")

    @Test
    internal fun `empty registry returns null`() {
        assertNull(createRegistry().value("key"))
    }

    @Test
    internal fun `registry returns null if edit operations did not complete`() {
        createRegistry().apply {
            edit().put("key", "value")
            assertNull(value("key"))
        }
    }

    @Test
    internal fun `registry returns actual value if edit operations was completed`() {
        createRegistry().apply {
            edit().use {
                it.put("key", "value")
            }
            assertEquals("value", value("key"))
        }
    }

    @Test
    internal fun `value in registry persisted across different registries`() {
        createRegistry().edit().use { it.put("key", "value") }
        assertEquals("value", createRegistry().value("key"))
    }

    @Test
    internal fun `empty registry has empty keys`() {
        assertEquals(0, createRegistry().keys().size)
    }

    @Test
    internal fun `registry with two values has two keys`() {
        createRegistry().apply {
            edit().use {
                it.put("key1", "value1")
                it.put("key2", "value2")
            }
            assertEquals(2, keys().size)
        }
    }

    @Test
    internal fun `registry has a previously added key`() {
        createRegistry().apply {
            edit().use {
                it.put("key1", "value1")
            }
            assertTrue(keys().contains("key1"))
        }
    }

    private fun createRegistry() = PathRegistry(root)
}