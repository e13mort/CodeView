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

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import com.github.e13mort.codeview.datasource.filesystem.PathSourceFile
import com.github.e13mort.codeview.datasource.filesystem.FSVisitor
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import io.reactivex.Single
import java.nio.file.Path
import javax.inject.Inject

class GitDataSource @Inject constructor(
    private val remoteRepositories: RemoteRepositories,
    private val sourcesUrl: SourcesUrl,
    private val localRepositories: LocalRepositories
) : DataSource {

    override fun name(): String {
        return "GIT"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.fromCallable {
            sourcesDescription(path)?.let {
                return@fromCallable GitSources(it, remoteRepositories, localRepositories, 1)
            }
            val pathDescription = sourcesUrl.parse(path) ?: throw IllegalArgumentException("path $path can not be parsed")
            throw IllegalArgumentException("invalid target branch ${pathDescription.readPart(Kind.BRANCH)}")
        }
    }

    override fun describeSources(source: SourcePath): String {
        return sourcesDescription(source)?.toString() ?: super.describeSources(source)
    }

    private fun sourcesDescription(source: SourcePath): GitSources.SourcesDescription? {
        val pathDescription =
            sourcesUrl.parse(source) ?: throw IllegalArgumentException("path $source can not be parsed")
        val targetRepo = pathDescription.readPart(Kind.GIT_URL_HTTPS)
        val pathInRepo = pathDescription.readPart(Kind.PATH)
        remoteRepositories.remoteBranchHash(pathDescription)?.let {
            return GitSources.SourcesDescription(targetRepo, it, pathInRepo)
        }
        return null
    }

    private class GitSources(
        private val description: SourcesDescription,
        private val remoteRepositories: RemoteRepositories,
        private val localRepositories: LocalRepositories,
        private val depth: Int
    ) : Sources {

        private val fsVisitor = FSVisitor()

        override fun sources(): List<SourceFile> {
            clonedRepo(searchForLocalRepository()).let {
                checkout(it)
                resolveSourceFolder(it)
            }.let {
                return visitFolder(it).map(::PathSourceFile)
            }
        }

        private fun checkout(clonedRepo: RemoteRepositories.ClonedRepo) = clonedRepo.checkout(description.cloneHash)

        private fun searchForLocalRepository(): Path = localRepositories.search(description.repoUrl)

        private fun clonedRepo(localPath: Path): RemoteRepositories.ClonedRepo {
            return remoteRepositories.clone(description.repoUrl, localPath)
        }

        private fun visitFolder(it: Path): List<Path> {
            return fsVisitor.visitFolder(it, depth)
        }

        private fun resolveSourceFolder(it: RemoteRepositories.ClonedRepo) = it.path().resolve(description.sourcesFolder)

        override fun name(): String {
            return description.toString()
        }

        data class SourcesDescription(
            val repoUrl: String,
            val cloneHash: String,
            val sourcesFolder: String
        )
    }
}