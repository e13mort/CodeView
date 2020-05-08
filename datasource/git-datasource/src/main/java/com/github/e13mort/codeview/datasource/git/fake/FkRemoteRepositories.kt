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

package com.github.e13mort.codeview.datasource.git.fake

import com.github.e13mort.codeview.datasource.git.RemoteRepositories
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import java.nio.file.Path

class FkRemoteRepositories : RemoteRepositories {
    private val repoWithBranchToHash: MutableMap<Pair<String, String>, String?> = mutableMapOf()
    private val repoWithHashToRepo: MutableMap<Pair<String, Path>, RemoteRepositories.ClonedRepo> = mutableMapOf()

    override fun remoteBranchHash(
        pathDescription: SourcesUrl.PathDescription
    ): String? {
        return repoWithBranchToHash[Pair(pathDescription.readPart(Kind.GIT_URL_HTTPS), pathDescription.readPart(Kind.BRANCH))]
    }

    override fun clone(repoUrl: String, path: Path): RemoteRepositories.ClonedRepo {
        return FkRepo(path, repoUrl)
    }

    fun add(repoUrl: String, branchName: String, hash: String?) {
        repoWithBranchToHash += Pair(Pair(repoUrl, branchName), hash)
    }

    private inner class FkRepo(private val path: Path, private val repoUrl: String) : RemoteRepositories.ClonedRepo {
        override fun checkout(hash: String) {
            repoWithHashToRepo += Pair(Pair(repoUrl, path), this)
        }

        override fun path(): Path = path
    }
}