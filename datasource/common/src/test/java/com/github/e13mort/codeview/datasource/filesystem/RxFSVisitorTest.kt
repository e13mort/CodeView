package com.github.e13mort.codeview.datasource.filesystem

import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.util.stream.Stream
import com.github.e13mort.codeview.datasource.filesystem.FSVisitor.Options as Opts

internal class FSVisitzorTest {
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
            Arguments.of(Opts("txt", 0), 0),
            Arguments.of(Opts("txt", 1), 2),
            Arguments.of(Opts("txt", 2), 4),
            Arguments.of(Opts("doc", 1), 0)
        )

    }

    @ParameterizedTest(name = "params {0} -> expected count: {1}")
    @MethodSource("args")
    internal fun test(options: Opts, expectedCount: Int) {
        val test = FSVisitor().visitFolder(root, options)
        assertEquals(expectedCount, test.size)
    }
}