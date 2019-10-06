package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.Sources
import com.github.e13mort.githuburl.GithubUrl
import com.github.e13mort.githuburl.GithubUrlImpl
import com.jcabi.github.Content
import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import com.jcabi.github.RtGithub
import io.reactivex.Observable
import io.reactivex.Single
import java.io.InputStream

class GithubDataSource(
    private val config: DataSourceConfig
) : DataSource {

    private lateinit var github: Github

    internal constructor(
        config: DataSourceConfig,
        github: Github
    ) : this(config) {
        this.github = github
    }

    init {
        if (!this::github.isInitialized) {
            this.github = RtGithub(config.key)
        }
    }

    override fun name(): String {
        return "github"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return prepareUrl(path).map {
            GithubSources(it, github, config.fileExtension)
        }
    }

    private fun prepareUrl(parameters: SourcePath): Single<PathParts> {
        return Single.just(GithubUrlImpl(parameters))
            .flatMap {
                val parse = it.parse()
                when {
                    parse != null -> Single.just(parse)
                    else -> Single.error(IllegalArgumentException("$it can't be parsed"))
                }
            }
            .filter { url ->
                url.hasPart(
                    GithubUrl.PathDescription.Kind.USER_NAME,
                    GithubUrl.PathDescription.Kind.PROJECT_NAME,
                    GithubUrl.PathDescription.Kind.BRANCH,
                    GithubUrl.PathDescription.Kind.PATH
                )
            }
            .switchIfEmpty(Single.error(IllegalArgumentException("Invalid github path $parameters")))
            .map { pathDescription ->
                PathParts(
                    userName = pathDescription.readPart(GithubUrl.PathDescription.Kind.USER_NAME),
                    projectName = pathDescription.readPart(GithubUrl.PathDescription.Kind.PROJECT_NAME),
                    path = pathDescription.readPart(GithubUrl.PathDescription.Kind.PATH),
                    branch = pathDescription.readPart(GithubUrl.PathDescription.Kind.BRANCH)
                )
            }
    }

    data class PathParts(
        val userName: String,
        val projectName: String,
        val path: String,
        val branch: String
    )

    data class DataSourceConfig(
        val fileExtension: String,
        val key: String = ""
    )
}

private class GithubSources(
    private val pathParts: GithubDataSource.PathParts,
    private val github: Github,
    private val fileExtension: String
) : Sources {

    override fun name(): String {
        return pathParts.toString()
    }

    override fun sources(): Observable<SourceFile> {
        return Observable.fromIterable(
            github.repos().get(Coordinates.Simple(pathParts.userName, pathParts.projectName))
                .contents().iterate(pathParts.path, pathParts.branch)
        )
            .filter { isItemFits(it) }
            .map { GithubSourceFile(it) }
    }

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