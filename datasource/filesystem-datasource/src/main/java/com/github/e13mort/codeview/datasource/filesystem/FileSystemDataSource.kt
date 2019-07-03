package com.github.e13mort.codeview.datasource.filesystem

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import io.reactivex.Observable
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.*

class FileSystemDataSource(private val root: String): DataSource {

    override fun name(): String {
        return "filesystem"
    }

    override fun sources(): Observable<SourceFile> {
        val javaFileVisitor = ExtensionBasedFileVisitor("java")
        Files.walkFileTree(Paths.get(root), javaFileVisitor)

        return Observable.fromIterable(javaFileVisitor.files)
            .map { PathSourceFile(it) }
    }

    class PathSourceFile(private val path: Path): SourceFile {
        override fun name(): String {
            return path.fileName.toString()
        }

        override fun read(): InputStream {
            return FileInputStream(path.toFile())
        }

    }

}