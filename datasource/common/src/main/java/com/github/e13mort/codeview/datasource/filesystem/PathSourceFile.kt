package com.github.e13mort.codeview.datasource.filesystem

import com.github.e13mort.codeview.SourceFile
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

class PathSourceFile(private val path: Path): SourceFile {
    override fun fileInfo(): SourceFile.FileInfo {
        return object : SourceFile.FileInfo {
            override fun lastModifiedDate(): Long = 0

        }
    }

    override fun name(): String {
        return path.fileName.toString()
    }

    override fun read(): InputStream {
        return FileInputStream(path.toFile())
    }

}