package com.github.e13mort.codeview.datasource.git.fake

import com.github.e13mort.codeview.datasource.git.RemoteRepositories
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import java.nio.file.Path

class FkRemoteRepositories : RemoteRepositories {
    private val repoWithBranchToHash: MutableMap<Pair<String, String>, String?> = mutableMapOf()
    private val repoWithHashToRepo: MutableMap<Pair<String, String>, RemoteRepositories.ClonedRepo> = mutableMapOf()

    override fun remoteBranchHash(
        pathDescription: SourcesUrl.PathDescription
    ): String? {
        return repoWithBranchToHash[Pair(pathDescription.readPart(Kind.GIT_URL_HTTPS), pathDescription.readPart(Kind.BRANCH))]
    }

    override fun clone(repoUrl: String, hash: String): RemoteRepositories.ClonedRepo {
        return repoWithHashToRepo[Pair(repoUrl, hash)]!!
    }

    fun add(repoUrl: String, branchName: String, hash: String?) {
        repoWithBranchToHash += Pair(Pair(repoUrl, branchName), hash)
    }

    fun add(repoUrl: String, hash: String, path: Path) {
        repoWithHashToRepo += Pair(Pair(repoUrl, hash), FkRepo(path))
    }

    private class FkRepo(private val path: Path) : RemoteRepositories.ClonedRepo {
        override fun path(): Path = path
    }
}