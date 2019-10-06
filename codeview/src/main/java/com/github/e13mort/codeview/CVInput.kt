package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path
import java.nio.file.Paths

interface CVInput {
    fun handleInput(path: SourcePath): Single<Path>
}

class PlainCVInput : CVInput {
    override fun handleInput(path: SourcePath): Single<Path> = Single.just(Paths.get(path))
}