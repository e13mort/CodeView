package com.github.e13mort.codeview.datasource.git

import java.nio.file.Path

interface LocalRepositories {
    fun search(repoUrl: String): Path
}