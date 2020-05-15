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

package com.github.e13mort.codeview.client.ktor.sources

import com.github.e13mort.codeview.asString
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.github.e13mort.githuburl.SourcesUrl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull

class ContentStorageSourcesUrl(private val contentStorage: KeyValueStorage) : SourcesUrl {

    override fun parse(path: String): SourcesUrl.PathDescription? {
        val content = contentStorage.searchSingleItem(path) ?: return null
        val json = Json(JsonConfiguration.Stable)

        val data = json.parse(JsonObject.serializer(), content.asString())
        return StoredSourcesUrlDescription(data)
    }

    override fun canParse(path: String): Boolean = true
}

private class StoredSourcesUrlDescription(private val map: JsonObject) : SourcesUrl.PathDescription {

    override fun readPart(kind: SourcesUrl.PathDescription.Kind): String {
        val jsonElement = map[kind.name.toLowerCase()]
        return jsonElement?.contentOrNull ?: error("There's no $kind")
    }

    override fun hasPart(vararg kinds: SourcesUrl.PathDescription.Kind?): Boolean {
        for (kind in kinds) {
            if (!map.containsKey(kind?.name?.toLowerCase())) return false
        }
        return true
    }
}
