package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.githuburl.SourcesUrl
import java.nio.file.Path

interface RemoteRepositories {
    fun remoteBranchHash(pathDescription: SourcesUrl.PathDescription): String?

    fun clone(repoUrl: String, hash: String): ClonedRepo

    interface ClonedRepo {
        fun path(): Path
    }
}