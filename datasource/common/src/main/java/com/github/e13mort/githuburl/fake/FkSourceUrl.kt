package com.github.e13mort.githuburl.fake

import com.github.e13mort.githuburl.SourcesUrl

class FkSourceUrl : SourcesUrl {
    private val pathToDescription = mutableMapOf<String, FkPathDescription>()

    override fun canParse(path: String): Boolean {
        return pathToDescription.containsKey(path)
    }

    override fun parse(path: String): SourcesUrl.PathDescription? {
        return pathToDescription[path]
    }

    fun add(path: String, kind: SourcesUrl.PathDescription.Kind, kindValue: String) {
        var description = pathToDescription[path]
        if (description == null) description = FkPathDescription().apply { pathToDescription[path] = this }
        description.add(kind, kindValue)
    }

    private class FkPathDescription : SourcesUrl.PathDescription {
        private val kindToPart = mutableMapOf<SourcesUrl.PathDescription.Kind, String>()

        override fun readPart(kind: SourcesUrl.PathDescription.Kind): String {
            return kindToPart[kind]!!
        }

        override fun hasPart(vararg kinds: SourcesUrl.PathDescription.Kind?): Boolean {
            for (kind in kinds) {
                if (!kindToPart.containsKey(kind)) return false
            }
            return true
        }

        fun add(kind: SourcesUrl.PathDescription.Kind, value: String) {
            kindToPart[kind] = value
        }

    }
}