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

import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

class JGitRemoteRepositories @Inject constructor() :
    RemoteRepositories {
    private var listener: RemoteRepositoryStateListener = RemoteRepositoryStateListener.EMPTY

    internal constructor(listener: RemoteRepositoryStateListener) : this() {
        this.listener = listener
    }

    override fun remoteBranchHash(
        pathDescription: SourcesUrl.PathDescription
    ): String? {
        val map = Git.lsRemoteRepository()
            .setRemote(pathDescription.readPart(Kind.GIT_URL_HTTPS))
            .callAsMap()
        return map["refs/heads/${pathDescription.readPart(Kind.BRANCH)}"]?.objectId?.name()
    }

    override fun clone(repoUrl: String, path: Path): RemoteRepositories.ClonedRepo {
        try {
            return if (repoCanBeUpdated(path))
                updateRepository(path)
            else
                cloneRepo(repoUrl, path)
        } catch (e: Exception) {
            throw RemoteRepositories.CloneException(e)
        }
    }

    private fun cloneRepo(
        repoUrl: String,
        path: Path
    ): ClonedRepo {
        val git = Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(path.toFile())
            .call()
            ?: throw IllegalStateException("Failed to clone repo. Returned git object is null")
        listener.onEvent(RemoteRepositoryStateListener.Event.CLONE)
        return ClonedRepo(path, git)
    }

    private fun updateRepository(path: Path): RemoteRepositories.ClonedRepo {
        val git = Git.open(path.toFile()) ?: throw IllegalStateException("Invalid repository. Returned git object is null")
        git.fetch().call() ?: throw IllegalStateException("Fetch operation failed. Returned object is null")
        listener.onEvent(RemoteRepositoryStateListener.Event.OPEN)
        return ClonedRepo(path, git)
    }

    private fun repoCanBeUpdated(path: Path): Boolean {
        return Files.exists(path.resolve(".git"))
    }

    private inner class ClonedRepo(private val path: Path, private val git: Git) :
        RemoteRepositories.ClonedRepo {
        override fun checkout(hash: String) {
            try {
                git
                    .reset()
                    .setRef(hash)
                    .setMode(ResetCommand.ResetType.HARD)
                    .call()
                listener.onEvent(RemoteRepositoryStateListener.Event.CHECK_OUT)
            } catch (e: Exception) {
                throw RemoteRepositories.CloneException(e)
            }
        }

        override fun path(): Path {
            return path
        }
    }

    interface RemoteRepositoryStateListener {
        enum class Event {CLONE, OPEN, CHECK_OUT}

        fun onEvent(event: Event)

        companion object EMPTY : RemoteRepositoryStateListener {
            override fun onEvent(event: Event) = Unit
        }
    }

}