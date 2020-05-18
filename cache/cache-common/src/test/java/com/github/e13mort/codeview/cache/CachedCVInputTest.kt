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

import com.github.e13mort.codeview.stubs.StubContent
import com.github.e13mort.codeview.stubs.StubDataSource
import com.github.e13mort.codeview.stubs.StubSourceFile
import com.github.e13mort.codeview.stubs.StubSources
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.io.InputStream

internal class CachedCVInputTest {

    private val root = Jimfs.newFileSystem().getPath(".")
    private val trackedInputStream = TrackedInputStream("fake-content".byteInputStream())

    @Test
    internal fun `empty cached input causes a read operation on source input stream`() {
        createEmptyInput(trackedInputStream)
            .prepare("does-not-matter")
            .flatMap { it.transform() }
            .test()
        assertNotEquals(0, trackedInputStream.readCounter)
    }

    @Test
    internal fun `cached input with cached item does not cause a read operation on source input stream`() {
        createCachedInput(trackedInputStream)
            .prepare("does-not-matter")
            .flatMap { it.transform() }
            .test()
        assertEquals(0, trackedInputStream.readCounter)
    }

    private fun createEmptyInput(inputStream: TrackedInputStream): CachedCVInput {
        return CachedCVInput(
            StubDataSource(StubSources(StubSourceFile(inputStream))),
            PathContentStorageStorage(root, UUIDCacheName(), PathRegistry(root.resolve("registry.json")))
        )
    }

    private fun createCachedInput(inputStream: TrackedInputStream): CachedCVInput {
        return CachedCVInput(
            StubDataSource(StubSources(StubSourceFile(inputStream))),
            PathContentStorageStorage(root, UUIDCacheName(), PathRegistry(root.resolve("registry.json"))).apply {
                prepareStorageItems("stub").apply {
                    put(StubContent())
                    save()
                }
            })
    }
}

internal class TrackedInputStream(private val inputStream: InputStream) : InputStream() {
    var readCounter = 0

    override fun read(): Int {
        readCounter++
        return inputStream.read()
    }

}