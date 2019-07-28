package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.SourceFile
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
        config: DataSourceConfig = DataSourceConfig("", ""),
        github: Github = RtGithub(config.key)
    ) : this(config) {
        this.github = github
    }

    override fun name(): String {
        return "github"
    }

    override fun sources(path: SourcePath): Observable<SourceFile> {
        return prepareUrl(path).flatMapObservable { pathParts ->
            Observable.fromIterable(github.repos().get(Coordinates.Simple(pathParts.userName, pathParts.projectName))
                .contents().iterate(pathParts.path, pathParts.branch))
                .filter { isItemFits(it) }
                .map { GithubSourceFile(it) }
        }
    }

    private fun isItemFits(it: Content) = it.path().endsWith(".${config.fileExtension}")

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
            .map { pathDescription -> PathParts(
                userName = pathDescription.readPart(GithubUrl.PathDescription.Kind.USER_NAME),
                projectName = pathDescription.readPart(GithubUrl.PathDescription.Kind.PROJECT_NAME),
                path = pathDescription.readPart(GithubUrl.PathDescription.Kind.PATH),
                branch = pathDescription.readPart(GithubUrl.PathDescription.Kind.BRANCH)
            ) }
    }

    data class PathParts(val userName: String,
                         val projectName: String,
                         val path: String,
                         val branch: String)

    data class DataSourceConfig(
        val fileExtension: String,
        val key: String = ""
    )
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