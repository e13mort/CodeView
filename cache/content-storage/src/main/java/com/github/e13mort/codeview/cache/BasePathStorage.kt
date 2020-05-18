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

import java.nio.file.Files
import java.nio.file.Path

open class BasePathStorage(
    private val root: Path,
    private val cacheName: CacheName,
    private val registry: PathRegistry
) {
    protected fun registerCacheFolder(key: String): Path {
        ensureRootExists()
        val folderName = cacheName.createDirName()
        val editableRegistry = registry.edit()
        editableRegistry.put(key, folderName)
        val path = root.resolve(folderName)
        Files.createDirectory(path)
        if (Files.exists(path)) editableRegistry.close()
        return path
    }

    protected fun registerCacheItem(key: String): Path {
        ensureRootExists()

        val folderName = cacheName.createDirName()
        val folderPath = root.resolve(folderName)
        Files.createDirectory(folderPath)
        val filePath = folderPath.resolve(cacheName.createFileName())
        val relativePath = root.relativize(filePath)

        registry.edit().use {
            it.put(key, relativePath.toString())
        }
        return filePath
    }

    protected fun folderName(key: String): String? {
        return registry.value(key)
    }

    private fun ensureRootExists() {
        if (!Files.exists(root)) Files.createDirectory(
            root
        )
    }
}