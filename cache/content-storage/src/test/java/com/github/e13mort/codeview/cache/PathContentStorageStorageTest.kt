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

import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.asContent
import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class PathContentStorageStorageTest {
    private lateinit var memoryFileSystem: FileSystem
    private lateinit var storage: PathContentStorageStorage
    private lateinit var root: Path
    private val cacheName = UUIDCacheName()

    companion object {
        const val REGISTRY_NAME = "reg.json"
    }

    @BeforeEach
    internal fun setUp() {
        memoryFileSystem = Jimfs.newFileSystem()
        root = memoryFileSystem.getPath(".")
        storage = PathContentStorageStorage(root, cacheName, PathRegistry(root.resolve(REGISTRY_NAME)))
    }

    @Test
    internal fun `there's no registry file after creation`() {
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).doesNotExist()
    }

    @Test
    internal fun `registry file created after the first insertion`() {
        putItems("some-key", MemoryContent())
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).exists()
    }

    @Test
    fun `registry file is not empty`() {
        putItems("some-key", MemoryContent())
        assertThat(Files.readAllLines(memoryFileSystem.getPath(REGISTRY_NAME))).isNotEmpty
    }

    @Test
    internal fun `there's no items found on empty storage`() {
        assertNull(storage.search("key"))
    }

    @Test
    internal fun `previously created item is found`() {
        putItems("key", MemoryContent())
        assertNotNull(storage.search("key"))
    }

    @Test
    internal fun `item is found on a new file storage`() {
        putItems("key", MemoryContent())
        val newStorage = PathContentStorageStorage(root, cacheName, PathRegistry(root.resolve(REGISTRY_NAME)))
        val key = "key"
        assertNotNull(newStorage.search(key))
    }

    @Test
    internal fun `directory created for a cached item`() {
        putItems("key", MemoryContent())
        assertTrue(Files.isDirectory(storage.search("key")!!.path()))
    }

    @Test
    internal fun `source file saved to cache`() {
        putItems("key", MemoryContent())
        assertEquals(1, Files.list(storage.search("key")!!.path()).toArray().size)
    }

    @Test
    fun `root folder contains folder with cache`() {
        val item = putItems("key", MemoryContent())
        assertThat(root.resolve(item)).exists()
    }

    @Test
    internal fun `two files are saved without errors`() {
        putItems("key", MemoryContent(), MemoryContent())
    }

    @Test
    internal fun `two files are exist in cache`() {
        val item = putItems("key", MemoryContent(), MemoryContent())
        assertEquals(2, Files.list(item).count())
    }

    @Test
    internal fun `cached item is equal to the original item`() {
        putItems("key", "hello".asContent())
        assertEquals("hello", storage.search("key")!!.content().read().reader().readText())
    }

    @Test
    internal fun `search returns empty result on not existing cache dir`() {
        val newStorage = PathContentStorageStorage(
            root.resolve("not_existing_dir"),
            cacheName,
            PathRegistry(root.resolve("not_existing_dir").resolve(REGISTRY_NAME))
        )
        assertNull(newStorage.search("key"))
    }

    @Test
    internal fun `removing multiple items leads to empty result`() {
        putItems("key", MemoryContent())
        storage.remove("key")
        assertNull(storage.search("key"))
    }

    @Test
    internal fun `removing multiple items leads actual files removal`() {
        putItems("key", MemoryContent())
        storage.remove("key")
        assertEquals(0L, root.internalDirsCount())
    }

    private fun putItems(
        key: String,
        vararg content: Content
    ): Path {
        return storage.prepareStorageItems(key).apply {
            content.forEach {
                put(it)
            }
        }.save()
    }

    internal class MemoryContent(private val bytes : ByteArray = byteArrayOf()) : Content {
        override fun read(): InputStream = ByteArrayInputStream(bytes)
    }
}
