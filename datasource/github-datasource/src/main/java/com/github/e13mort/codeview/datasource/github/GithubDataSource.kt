package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile

class GithubDataSource : DataSource {
    override fun name(): String {
        return "github"
    }

    override fun sources(): List<SourceFile> {
        return emptyList()
    }
}