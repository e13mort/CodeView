package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.githuburl.SourcesUrl

interface PathPartsTransformation {
    fun transformSourcePath(path: SourcePath): SourcesUrl.PathDescription
}