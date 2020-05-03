package com.github.e13mort.codeview.client.ktor.sources

import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.githuburl.SourcesUrl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull

class ContentStorageSourcesUrl(private val contentStorage: ContentStorage<out Any>) : SourcesUrl {

    override fun parse(path: String): SourcesUrl.PathDescription? {
        val singleItem = contentStorage.searchSingleItem(path) ?: return null
        val json = Json(JsonConfiguration.Stable)
        val content = singleItem.content().read().reader().readText()

        val data = json.parse(JsonObject.serializer(), content)
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
