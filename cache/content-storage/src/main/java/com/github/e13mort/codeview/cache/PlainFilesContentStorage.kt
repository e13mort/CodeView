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
import java.nio.file.StandardCopyOption

class PlainFilesKeyValueStorage(private val dataDir: Path) : KeyValueStorage {
    override fun put(key: String, content: Content) {
        Files.copy(
            content.read(),
            dataDir.ensureExists().resolve(key),
            StandardCopyOption.REPLACE_EXISTING)
    }

    override fun data(): Map<String, Content> {
        val result = mutableMapOf<String, Content>()
        Files.list(dataDir).map {
            it.fileName.toString() to PathBasedContent(it)
        }.forEach {
            result[it.first] = it.second
        }
        return result
    }

    override fun search(key: String): Content? {
        val path = dataDir.resolve(key)
        return if (Files.isReadable(path)) PathBasedContent(path) else null
    }

    override fun remove(key: String) {
        Files.delete(dataDir.resolve(key))
    }
}