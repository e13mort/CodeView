package com.github.e13mort.codeview.datasource.filesystem

import com.github.e13mort.codeview.SourceFile
import org.junit.jupiter.api.*
import java.nio.file.Files
import java.nio.file.Path

internal class FileSystemDataSourceTest {

    @Nested
    @DisplayName("FileSystem data source based on plain directory")
    inner class PlainDirectory {

        private lateinit var dir: Path

        @BeforeEach
        internal fun setUp() {
            dir = Files.createTempDirectory("tmp")
            Files.createTempFile(dir, "file1", ".java")
            Files.createTempFile(dir, "file2", ".java")
        }

        @DisplayName("There's two files available")
        @Test
        internal fun twoFiles() {
            val fileSystemDataSource = FileSystemDataSource()
            val test = fileSystemDataSource.testSources(dir.toFile().absolutePath)
            Assertions.assertEquals(2, test.size)
        }

    }

    @Nested
    @DisplayName("FileSystem data source based on a directory with subdirectories")
    inner class TreeDirectory {

        private lateinit var dir: Path

        @BeforeEach
        internal fun setUp() {
            dir = Files.createTempDirectory("tmp")
            Files.createTempFile(dir, "file1", ".java")
            Files.createTempFile(dir, "file2", ".java")
            val child1 = Files.createTempDirectory(dir, "level1")
            Files.createTempFile(child1, "file1", ".java")
        }

        @DisplayName("There's two files available")
        @Test
        internal fun twoFiles() {
            val fileSystemDataSource = FileSystemDataSource()
            val test = fileSystemDataSource.testSources(dir.toFile().absolutePath)
            Assertions.assertEquals(2, test.size)
        }

    }
}

fun FileSystemDataSource.testSources(path: String): List<SourceFile> {
    return this.sources(path).map { it.sources() }.blockingGet()
}