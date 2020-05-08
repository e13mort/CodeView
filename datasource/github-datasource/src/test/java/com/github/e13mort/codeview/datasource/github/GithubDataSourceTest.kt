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

import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import com.jcabi.github.Contents
import com.jcabi.github.Repos
import com.jcabi.github.mock.MkGithub
import com.nhaarman.mockitokotlin2.any
import io.reactivex.Observable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.spi.JsonProvider

class GithubDataSourceTest {

    @Nested
    @DisplayName("Github data source with a single item")
    inner class OneItem {

        @DisplayName("There's only one item emitted")
        @Test
        internal fun onItemEmitted() {
            val test = githubDataSource().testSources().test()
            test.assertValueCount(1)
        }

        @DisplayName("The emitted item has valid name")
        @Test
        internal fun correctName() {
            val test = githubDataSource().testSources().test()
            test.assertValue { it.name() == "src/test.java" }
        }

        private fun githubDataSource(): GithubDataSource {
            return GithubDataSource(
                GithubDataSource.DataSourceConfig("java"), prepareSingleItem(),
                transformation()
            )
        }

    }

    @Nested
    @DisplayName("Github data source with 3 java items")
    inner class MultipleJavaItems {
        @Test
        internal fun multipleJavaItems() {
            val source =
                GithubDataSource(GithubDataSource.DataSourceConfig("java"), prepareThreeItem(), transformation())
            val test = source.testSources().test()
            test.assertValueCount(3)
        }
    }

    @Nested
    @DisplayName("Github data source with 3 java items along with other files")
    inner class MultipleMixedItems {
        @Test
        internal fun multipleJavaItems() {
            val source =
                GithubDataSource(GithubDataSource.DataSourceConfig("java"), prepareThreeJavaItem(), transformation())
            val test = source.testSources().test()
            test.assertValueCount(3)
        }
    }

    private fun transformation(): PathPartsTransformation {
        return object : PathPartsTransformation {
            override fun transformSourcePath(path: SourcePath): SourcesUrl.PathDescription {
                val description = mock(SourcesUrl.PathDescription::class.java)
                `when`(description.hasPart(any())).thenReturn(true)
                `when`(description.readPart(ArgumentMatchers.eq(Kind.PATH))).thenReturn("src")
                `when`(description.readPart(ArgumentMatchers.eq(Kind.BRANCH))).thenReturn("master")
                `when`(description.readPart(ArgumentMatchers.eq(Kind.USER_NAME))).thenReturn("e13mort")
                `when`(description.readPart(ArgumentMatchers.eq(Kind.PROJECT_NAME))).thenReturn("testRepo")
                return description
            }

        }
    }

    private fun prepareSingleItem(): MkGithub {
        return prepareGithub {
            it.create(prepareFileJsonObject("test", "src/test.java", "This is a test content"))
        }
    }

    fun prepareThreeItem(): MkGithub {
        return prepareGithub {
            it.create(prepareFileJsonObject("test", "src/test.java"))
            it.create(prepareFileJsonObject("ClassA", "src/ClassA.java"))
            it.create(prepareFileJsonObject("ClassB", "src/ClassB.java"))
        }
    }

    fun prepareThreeJavaItem(): MkGithub {
        return prepareGithub {
            it.create(prepareFileJsonObject("test", "src/test.java"))
            it.create(prepareFileJsonObject("ClassA", "src/ClassA.java"))
            it.create(prepareFileJsonObject("ClassB", "src/ClassB.java"))
            it.create(prepareFileJsonObject("KotlinClass", "src/ClassD.kt"))
            it.create(prepareFileJsonObject("CppFile", "src/ClassE.cpp"))
            it.create(prepareFileJsonObject("CppFileHeader", "src/ClassE.h"))
        }
    }

    private fun prepareGithub(fill: (Contents) -> Unit): MkGithub {
        val github = MkGithub("e13mort")
        val createRepo = Repos.RepoCreate("testRepo", false)
        val testRepo = github.repos().create(createRepo)
        val contents = testRepo.contents()
        fill(contents)
        return github
    }

    private fun prepareFileJsonObject(name: String, path: String, content: String = ""): JsonObject? {
        return JsonProvider.provider().createObjectBuilder()
            .file(name, path, content)
            .build()
    }

    private fun JsonObjectBuilder.file(name: String, path: String, content: String = ""): JsonObjectBuilder {
        return add("name", name)
            .add("path", path)
            .add("content", content)
            .add("message", "test message")
    }
}

fun GithubDataSource.testSources(): Observable<SourceFile> {
    return Observable.fromIterable(this.sources("does-not-matter").blockingGet().sources())
}