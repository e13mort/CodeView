package com.github.e13mort.codeview.datasource.filesystem

import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ExtensionBasedFileVisitor(private val ext: String) : SimpleFileVisitor<Path>() {

    internal val files = ArrayList<Path>()

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        if (isValid(file)) files.add(file)
        return FileVisitResult.CONTINUE
    }

    private fun isValid(file: Path) = file.toString().endsWith(".$ext")
}