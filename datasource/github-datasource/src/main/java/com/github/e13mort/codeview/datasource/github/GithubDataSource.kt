package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.Sources
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import com.jcabi.github.Content
import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import io.reactivex.Observable
import io.reactivex.Single
import java.io.InputStream
import javax.inject.Inject

class GithubDataSource @Inject constructor(
    private val config: DataSourceConfig,
    private val github: Github,
    private val pathPartsTransformation: PathPartsTransformation
) : DataSource {

    override fun name(): String {
        return "github"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.fromCallable { pathPartsTransformation.transformSourcePath(path) }
            .map { GithubSources(it, github, config.fileExtension)
        }
    }

    data class DataSourceConfig(
        val fileExtension: String
    )
}

private class GithubSources(
    private val pathParts: SourcesUrl.PathDescription,
    private val github: Github,
    private val fileExtension: String
) : Sources {

    override fun name(): String {
        return pathParts.toString()
    }

    override fun sources(): List<SourceFile> {
        return github
            .repos()
            .get(Coordinates.Simple(part(Kind.USER_NAME), part(Kind.PROJECT_NAME)))
            .contents()
            .iterate(part(Kind.PATH), part(Kind.BRANCH))
            .filter { isItemFits(it) }
            .map { GithubSourceFile(it) }
    }

    private fun part(kind: Kind) = pathParts.readPart(kind)

    private fun isItemFits(it: Content) = it.path().endsWith(".$fileExtension")

}

private class GithubSourceFile(private val githubContent: Content) : SourceFile {
    override fun fileInfo(): SourceFile.FileInfo {
        return object : SourceFile.FileInfo {
            override fun lastModifiedDate(): Long = 0
        }
    }

    override fun name(): String {
        return githubContent.path()
    }

    override fun read(): InputStream {
        return githubContent.raw()
    }

}