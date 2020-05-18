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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class PathBasedStorage(
    private val root: Path,
    private val cacheName: CacheName,
    private val registry: PathRegistry
) :
    ContentStorage<Path>, KeyValueStorage {

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

    override fun putSingleItem(key: String, content: Content) {
        registerCacheItem(key).apply {
            Files.copy(content.read(), this)
        }
    }

    override fun searchSingleItem(key: String): Content? {
        folderName(key)?.apply {
            val path = root.resolve(this)
            if (Files.exists(path)) {
                return PathBasedStorageItem(path)
            }
        }
        return null
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
            } else { //an item was placed via `single` methods
                Files.delete(path)
                Files.delete(path.parent)
            }
        }
    }

    private fun copyFileToCache(content: Content, parent: Path): Path {
        Files.copy(content.read(), parent.resolve(cacheName.createFileName()))
        return parent
    }

    private fun registerCacheFolder(key: String): Path {
        ensureRootExists()
        val folderName = cacheName.createDirName()
        val editableRegistry = registry.edit()
        editableRegistry.put(key, folderName)
        val path = root.resolve(folderName)
        Files.createDirectory(path)
        if (Files.exists(path)) editableRegistry.close()
        return path
    }

    private fun registerCacheItem(key: String): Path {
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

    private fun ensureRootExists() {
        if (!Files.exists(root)) Files.createDirectory(root)
    }

    private fun folderName(key: String): String? {
        return registry.value(key)
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

@Serializable
internal data class CachedMap(val data: MutableMap<String, String>)

internal fun MutableMap<String, String>.asJson(): String {
    val json = Json(configuration = JsonConfiguration.Stable)
    return json.toJson(CachedMap.serializer(), CachedMap(this)).toString()
}