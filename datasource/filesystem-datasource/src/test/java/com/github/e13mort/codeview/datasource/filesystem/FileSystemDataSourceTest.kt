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