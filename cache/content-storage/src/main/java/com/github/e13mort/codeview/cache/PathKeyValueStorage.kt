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
import java.nio.file.Files
import java.nio.file.Path

class PathKeyValueStorage(
    private val root: Path,
    cacheName: CacheName,
    private val registry: PathRegistry
): KeyValueStorage, BasePathStorage(root, cacheName, registry) {

    override fun put(key: String, content: Content) {
        registerCacheItem(key).apply {
            Files.copy(content.read(), this)
        }
    }

    override fun search(key: String): Content? {
        folderName(key)?.apply {
            val path = root.resolve(this)
            if (Files.exists(path)) {
                return PathContentStorageStorage.PathBasedStorageItem(path)
            }
        }
        return null
    }

    override fun remove(key: String) {
        folderName(key)?.apply {
            registry.edit().apply { remove(key) }
            root.resolve(this).apply {
                delete()
                deleteParent()
            }
        }
    }
}

private fun Path.delete() {
    Files.delete(this)
}

private fun Path.deleteParent() {
    Files.delete(this.parent)
}