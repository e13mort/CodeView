package com.github.e13mort.codeview.datasource.git

import java.nio.file.Path
import java.util.*

class FsLocalRepositories(private val root: Path) : LocalRepositories {

    override fun search(repoUrl: String): Path {
        return root.resolve(convertUrlToBase64(repoUrl))
    }

    private fun convertUrlToBase64(repoUrl: String) = Base64.getEncoder().encodeToString(repoUrl.toByteArray())
}