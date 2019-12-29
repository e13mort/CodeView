package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.codeview.datasource.git.fake.FkLocalRepositories
import com.github.e13mort.codeview.datasource.git.fake.FkRemoteRepositories
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import com.github.e13mort.githuburl.fake.FkSourceUrl
import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

internal class GitDataSourceTest {

    private lateinit var gitDataSource: GitDataSource

    @BeforeEach
    internal fun setUp() {
        val root = preparePath()
        val remoteRepositories = FkRemoteRepositories().apply {
            add("repo1.git", "master", "hash1")
            add("repo1.git", "invalid", null)
        }

        val sourcesUrl = FkSourceUrl().apply {
            add("path1", Kind.PATH, ".")
            add("path1", Kind.GIT_URL_HTTPS, "repo1.git")
            add("path1", Kind.BRANCH, "master")

            add("path2", Kind.PATH, ".")
            add("path2", Kind.GIT_URL_HTTPS, "repo1.git")
            add("path2", Kind.BRANCH, "invalid_branch")
        }

        val localRepositories = FkLocalRepositories().apply {
            add("repo1.git", root)
        }

        gitDataSource = GitDataSource(remoteRepositories, sourcesUrl, localRepositories)
    }

    @Test
    internal fun `one item emitted for a valid path`() {
        val test = gitDataSource.sources("path1").test()
        test.assertValueCount(1)
    }

    @Test
    internal fun `Illegal argument exception emitted on invalid path`() {
        val test = gitDataSource.sources("invalid_path").test()
        test.assertError(IllegalArgumentException::class.java)
    }

    @Test
    internal fun `Illegal argument exception emitted on path with different branch`() {
        val test = gitDataSource.sources("path2").test()
        test.assertError(IllegalArgumentException::class.java)
    }

    @Test
    internal fun `there is one item sources provided by valid path`() {
        val sources = gitDataSource.sources("path1").blockingGet()!!
        val test = sources.sources().test()
        test.assertValueCount(1)
    }

    @Test
    internal fun `there is no errors in sources provided by valid path`() {
        val sources = gitDataSource.sources("path1").blockingGet()!!
        val test = sources.sources().test()
        test.assertNoErrors()
    }

    @Test
    internal fun `there is one valid source file in a valid repository`() {
        val sources = gitDataSource.sources("path1").blockingGet()!!
        val sourceFile = sources.sources().blockingFirst()!!
        Assertions.assertThat(sourceFile.name()).isEqualTo("file1.java")
    }


    //fs:
    //root
    //-file1.java
    //-file1.kt
    //-file1.bin
    //-dir
    //--file2.java
    //--file2.kt
    //--file2.bin
    private fun preparePath(): Path {
        val root = Jimfs.newFileSystem().getPath(".")
        val dir = root.resolve("directory")
        Files.createDirectory(dir)
        Files.createFile(root.resolve("file1.java"))
        Files.createFile(root.resolve("file1.kt"))
        Files.createFile(root.resolve("file1.bin"))
        Files.createFile(dir.resolve("file2.java"))
        Files.createFile(dir.resolve("file2.kt"))
        Files.createFile(dir.resolve("file2.bin"))
        return root
    }
}