package com.github.e13mort.codeview.datasource.github

import com.jcabi.github.Contents
import com.jcabi.github.Repos
import com.jcabi.github.mock.MkGithub
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
            val dataSource = GithubDataSource(GithubDataSource.DataSourceConfig("java"), prepareSingleItem())
            val test = dataSource.sources("https://github.com/e13mort/testRepo/tree/master/src").test()
            test.assertValueCount(1)
        }

        @DisplayName("The emitted item has valid name")
        @Test
        internal fun correctName() {
            val dataSource = GithubDataSource(GithubDataSource.DataSourceConfig("java"), prepareSingleItem())
            val test = dataSource.sources("https://github.com/e13mort/testRepo/tree/master/src").test()
            test.assertValue { it.name() == "src/test.java" }
        }

    }

    @Nested
    @DisplayName("Github data source with 3 java items")
    inner class MultipleJavaItems {
        @Test
        internal fun multipleJavaItems() {
            val source =
                GithubDataSource(GithubDataSource.DataSourceConfig("java"), prepareThreeItem())
            val test = source.sources("https://github.com/e13mort/testRepo/tree/master/src").test()
            test.assertValueCount(3)
        }
    }

    @Nested
    @DisplayName("Github data source with 3 java items along with other files")
    inner class MultipleMixedItems {
        @Test
        internal fun multipleJavaItems() {
            val source =
                GithubDataSource(GithubDataSource.DataSourceConfig("java"), prepareThreeJavaItem())
            val test = source.sources("https://github.com/e13mort/testRepo/tree/master/src").test()
            test.assertValueCount(3)
        }
    }

    fun prepareSingleItem(): MkGithub {
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