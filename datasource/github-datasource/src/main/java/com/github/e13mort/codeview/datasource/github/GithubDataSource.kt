package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.jcabi.github.Content
import com.jcabi.github.Coordinates
import com.jcabi.github.RtGithub
import java.io.InputStream

class GithubDataSource : DataSource {
    override fun name(): String {
        return "github"
    }

    override fun sources(): List<SourceFile> {
        val mutableListOf = mutableListOf<SourceFile>()

        RtGithub().repos().get(object: Coordinates {
            override fun user(): String = TODO()

            override fun compareTo(other: Coordinates?): Int = 0

            override fun repo(): String = TODO()

        }).contents().iterate(TODO(), TODO()).forEach {
            mutableListOf.add(GithubSourceFile(it))
        }

        return mutableListOf
    }
}

private class GithubSourceFile(private val githubContent: Content) : SourceFile {
    override fun name(): String {
        return githubContent.path()
    }

    override fun read(): InputStream {
        return githubContent.raw()
    }

}