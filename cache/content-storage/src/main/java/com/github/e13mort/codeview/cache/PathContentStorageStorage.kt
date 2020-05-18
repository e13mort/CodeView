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

class PathContentStorageStorage(
    private val root: Path,
    private val cacheName: CacheName,
    private val registry: PathRegistry
) :
    ContentStorage<Path>, BasePathStorage(root, cacheName, registry) {

    override fun search(key: String): PathBasedStorageItem? {
        val folderName = folderName(key)
        folderName?.let {
            val path = root.resolve(it)
            if (Files.exists(path)) return PathBasedStorageItem(path)
        }
        return null
    }

    override fun prepareStorageItems(key: String): ContentStorage.StorageItems<Path> {
        return StorageItemsImpl(key)
    }

    private inner class StorageItemsImpl(private val key: String) : ContentStorage.StorageItems<Path> {

        private val path by lazy {
            registerCacheFolder(key)
        }

        override fun put(content: Content) {
            copyFileToCache(content, path)
        }

        override fun save(): Path {
            return path
        }

    }

    override fun remove(key: String) {
        folderName(key)?.apply {
            registry.edit().apply { remove(key) }
            val path = root.resolve(this)
            if (Files.isDirectory(path)) { //items was placed via reactive methods
                Files
                    .walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach { Files.delete(it) }
            }
        }
    }

    private fun copyFileToCache(content: Content, parent: Path): Path {
        Files.copy(content.read(), parent.resolve(cacheName.createFileName()))
        return parent
    }

    class PathBasedStorageItem(private val path: Path) :
        ContentStorage.ContentStorageItem, Content {
        override fun content(): Content = this

        fun path(): Path = path

        override fun read(): InputStream {
            if (Files.isDirectory(path)) {
                return Files.list(path).findFirst().map { PathBasedContent(it) }.get().read()
            }
            return PathBasedContent(path).read()
        }
    }

    private class PathBasedContent(private val path: Path) : Content {
        override fun read(): InputStream = Files.newInputStream(path)

    }

}