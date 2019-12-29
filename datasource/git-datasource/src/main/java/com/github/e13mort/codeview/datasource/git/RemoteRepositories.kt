package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.githuburl.SourcesUrl
import java.nio.file.Path

interface RemoteRepositories {
    fun remoteBranchHash(pathDescription: SourcesUrl.PathDescription): String?

    fun clone(repoUrl: String, path: Path): ClonedRepo

    interface ClonedRepo {
        fun path(): Path

        fun checkout(hash: String)
    }

    class CloneException(cause: Throwable?) : Exception(cause)
}