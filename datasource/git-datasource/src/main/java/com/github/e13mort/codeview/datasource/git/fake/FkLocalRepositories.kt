package com.github.e13mort.codeview.datasource.git.fake

import com.github.e13mort.codeview.datasource.git.LocalRepositories
import java.nio.file.Path

class FkLocalRepositories : LocalRepositories {
    private val urlToPathMap = mutableMapOf<String, Path>()

    override fun search(repoUrl: String): Path {
        return urlToPathMap[repoUrl]!!
    }

    fun add(repoUrl: String, path: Path) {
        urlToPathMap[repoUrl] = path
    }
}