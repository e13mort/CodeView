package com.github.e13mort.codeview.datasource.filesystem

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class FSVisitor {
    fun visitFolder(folder: Path, options: Options): List<Path> {
        return Files
            .walk(folder, options.depth)
            .filter { isValidName(it, options) }
            .toList()
    }

    private fun isValidName(
        it: Path,
        options: Options
    ) = !Files.isDirectory(it) && it.fileName.toString().endsWith(".${options.fileExtension}")

    data class Options(val fileExtension: String, val depth: Int)
}