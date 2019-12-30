package com.github.e13mort.codeview.datasource.git

import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

internal class FsLocalRepositoriesTest {
    private val root = Jimfs.newFileSystem().getPath("root").createDir()

    @Test
    internal fun `returned path has valid root`() {
        val repositories = FsLocalRepositories(root)
        val path = repositories.search("https://some.git.url/repo.git").createDir()
        assertThat(path).hasParent(root)
    }
}

fun Path.createDir(): Path {
    Files.createDirectory(this)
    return this
}