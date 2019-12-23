package com.github.e13mort.codeview.datasource.filesystem

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.Sources
import io.reactivex.Observable
import io.reactivex.Single
import java.nio.file.*
import java.util.*
import kotlin.collections.ArrayList

class FileSystemDataSource: DataSource {

    companion object {
        const val MAX_DEPTH = 1
    }

    override fun name(): String {
        return "filesystem"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.fromCallable {
            val javaFileVisitor = ExtensionBasedFileVisitor("java")
            Files.walkFileTree(Paths.get(path), EnumSet.noneOf(FileVisitOption::class.java), MAX_DEPTH, javaFileVisitor)

            FileSystemSources(path, javaFileVisitor.files)
        }
    }

    private class FileSystemSources(private val path: SourcePath, private val files: ArrayList<Path>) : Sources {

        override fun name(): String {
            return path
        }

        override fun sources(): Observable<SourceFile> {
            return Observable.fromIterable(files).map {
                PathSourceFile(it)
            }
        }

    }

}