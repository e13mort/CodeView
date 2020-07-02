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

import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.util.stream.Stream

internal class FSVisitorTest {
    private val root = Jimfs.newFileSystem().getPath(".")

    @BeforeEach
    internal fun setUp() {
        Files.createFile(root.resolve("file1.txt"))
        Files.createFile(root.resolve("file2.txt"))
        val directory = root.resolve("dir")
        Files.createDirectory(directory)
        Files.createFile(directory.resolve("file1.txt"))
        Files.createFile(directory.resolve("file2.txt"))
        Files.createDirectory(root.resolve("dir_with_extension.txt"))
    }

    companion object {
        @JvmStatic
        fun args(): Stream<Arguments> = Stream.of(
            Arguments.of( 0, 0),
            Arguments.of( 1, 2),
            Arguments.of( 2, 4)
        )

    }

    @ParameterizedTest(name = "params {0} -> expected count: {1}")
    @MethodSource("args")
    internal fun test(depth: Int, expectedCount: Int) {
        val test = FSVisitor().visitFolder(root, depth)
        assertEquals(expectedCount, test.size)
    }
}