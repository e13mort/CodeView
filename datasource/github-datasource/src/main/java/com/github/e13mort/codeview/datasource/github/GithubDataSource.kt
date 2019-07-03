package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.jcabi.github.Content
import com.jcabi.github.Coordinates
import com.jcabi.github.RtGithub
import io.reactivex.Observable
import java.io.InputStream

class GithubDataSource(
    private val config: DataSourceConfig) : DataSource {

    override fun name(): String {
        return "github"
    }

    override fun sources(): Observable<SourceFile> {

        val github = RtGithub(config.key)
        return Observable.fromIterable(github.repos().get(Coordinates.Simple(config.userName, config.projectName))
            .contents().iterate(config.path, config.branch))
            .filter { isItemFits(it) }
            .map { GithubSourceFile(it) }
    }

    private fun isItemFits(it: Content) = it.path().endsWith(".${config.fileExtension}")

    data class DataSourceConfig(val key: String,
                                val fileExtension: String,
                                val userName: String,
                                val projectName: String,
                                val path: String,
                                val branch: String)
}

private class GithubSourceFile(private val githubContent: Content) : SourceFile {
    override fun name(): String {
        return githubContent.path()
    }

    override fun read(): InputStream {
        return githubContent.raw()
    }

}