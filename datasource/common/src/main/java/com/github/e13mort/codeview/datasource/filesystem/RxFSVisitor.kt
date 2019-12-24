package com.github.e13mort.codeview.datasource.filesystem

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.nio.file.Files
import java.nio.file.Path

class RxFSVisitor {
    fun visitFolder(folder: Path, options: Options): Observable<Path> {
        return Observable.create { emitter: ObservableEmitter<Path> ->
            Files
                .walk(folder, options.depth)
                .filter { isValidName(it, options) }
                .forEach { emitter.onNext(it) }
            emitter.onComplete()
        }
    }

    private fun isValidName(
        it: Path,
        options: Options
    ) = !Files.isDirectory(it) && it.fileName.toString().endsWith(".${options.fileExtension}")

    data class Options(val fileExtension: String, val depth: Int)
}