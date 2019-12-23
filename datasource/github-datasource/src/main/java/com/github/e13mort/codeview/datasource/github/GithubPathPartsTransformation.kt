package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.githuburl.SourcesUrl

class GithubPathPartsTransformation(private val sourcesUrl: SourcesUrl):
    PathPartsTransformation {
    override fun transformSourcePath(path: SourcePath): SourcesUrl.PathDescription {
        sourcesUrl.parse(path)?.let {
            if (hasAllParts(it)) return it
        }
        throw IllegalArgumentException()
    }

    private fun hasAllParts(description: SourcesUrl.PathDescription) =
        description.hasPart(*SourcesUrl.PathDescription.Kind.values())
}