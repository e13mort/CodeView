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