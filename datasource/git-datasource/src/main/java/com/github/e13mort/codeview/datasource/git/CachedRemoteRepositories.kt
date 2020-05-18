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
package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.codeview.asContent
import com.github.e13mort.codeview.asString
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind

internal class CachedRemoteRepositories(private val source: RemoteRepositories, private val storage: KeyValueStorage) :
    RemoteRepositories by source {

    override fun remoteBranchHash(pathDescription: SourcesUrl.PathDescription): String? {
        val key = calculateKey(pathDescription) ?: return null
        storage.search(key)?.let {
            return it.asString()
        }
        return source.remoteBranchHash(pathDescription)?.also {
            storage.put(key, it.asContent())
        }
    }

    private fun calculateKey(description: SourcesUrl.PathDescription): String? {
        description.apply {
            return when {
                hasPart(Kind.GIT_URL_HTTPS, Kind.BRANCH) -> "${readPart(Kind.GIT_URL_HTTPS)}-${readPart(Kind.BRANCH)}"
                else -> null
            }
        }
    }
}

fun RemoteRepositories.cached(storage: KeyValueStorage) : RemoteRepositories {
    return CachedRemoteRepositories(this, storage)
}