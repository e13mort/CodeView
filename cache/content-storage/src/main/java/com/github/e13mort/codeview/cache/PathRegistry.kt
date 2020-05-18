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

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.nio.file.Files
import java.nio.file.Path

/*
    A simple thread unsafe registry implementation.
    It is based on underlying file passed as registryPath parameter.
 */
class PathRegistry(private val registryPath: Path) :
    Registry {
    override fun edit(): Registry.EditableRegistry {
        return PathEditableRegistry(registryPath, readMap())
    }

    override fun value(key: String): String? {
        return readMap()[key]
    }

    private fun readMap(): MutableMap<String, String> {
        return if (Files.exists(registryPath)) {
            readRegistry(registryPath)
        } else {
            mutableMapOf()
        }
    }

    private fun readRegistry(path: Path): MutableMap<String, String> {
        val readAllLines = Files.readAllLines(path)
        val combined = readAllLines.reduce { acc, s -> acc.plus(s) }
        val json =
            Json(configuration = JsonConfiguration.Stable)
        return json.parse(CachedMap.serializer(), combined).data
    }

    internal class PathEditableRegistry(private val path: Path, map: Map<String, String>) :
        Registry.EditableRegistry {
        private val mutableMap = map.toMutableMap()

        override fun put(key: String, value: String) {
            mutableMap[key] = value
        }

        override fun remove(key: String) {
            mutableMap.remove(key)
        }

        override fun close() {
            Files.write(path, listOf(mutableMap.asJson()))
        }
    }


}

@Serializable
private data class CachedMap(val data: MutableMap<String, String>)

private fun MutableMap<String, String>.asJson(): String {
    val json = Json(configuration = JsonConfiguration.Stable)
    return json.toJson(CachedMap.serializer(), CachedMap(this)).toString()
}
