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
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class PathKeyValueStorage(
    private val root: Path,
    private val cacheName: CacheName,
    private val registry: PathRegistry
): KeyValueStorage {

    override fun put(key: String, content: Content) {
        registerCacheItem(key).apply {
            Files.copy(content.read(), this)
        }
    }

    override fun search(key: String): Content? {
        registry.value(key)?.apply {
            val path = root.resolve(this)
            if (Files.exists(path)) {
                return PathBasedContent(path)
            }
        }
        return null
    }

    override fun remove(key: String) {
        registry.value(key)?.apply {
            registry.edit().apply { remove(key) }
            root.resolve(this).apply {
                delete()
                deleteParent()
            }
        }
    }

    private fun registerCacheItem(key: String): Path {
        return prepareFilePath().also { filePath ->
            registry.edit().use { editableRegistry ->
                editableRegistry.put(key, root.relativize(filePath).toString())
            }
        }
    }

    private fun prepareFilePath(): Path {
        return root.ensureExists()
            .resolve(cacheName.createDirName())
            .also { Files.createDirectory(it) }
            .resolve(cacheName.createFileName())
    }

    private class PathBasedContent(private val path: Path) : Content {
        override fun read(): InputStream = Files.newInputStream(path)
    }
}