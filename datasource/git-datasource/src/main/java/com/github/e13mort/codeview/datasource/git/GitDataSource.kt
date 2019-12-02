package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import io.reactivex.Single

class GitDataSource: DataSource {
    override fun name(): String {
        return "JGIT"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.error(Exception("Not implemented yet"))
    }
}