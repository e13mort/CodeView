/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.e13mort.codeview.datasource.github

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import com.jcabi.github.Content
import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import io.reactivex.Single
import java.io.InputStream

class GithubDataSource(
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