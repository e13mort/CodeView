package com.github.e13mort.codeview.datasource.filesystem

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile

class FileSystemDataSource: DataSource {
    override fun name(): String {
        return "filesystem"
    }

    override fun sources(): List<SourceFile> {
        return emptyList()
    }

}