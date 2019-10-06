package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path
import java.nio.file.Paths

interface CVInput {
    object EMPTY: CVInput {
        override fun handleInput(path: SourcePath): Single<Path> {
            return Single.just(Paths.get("empty"))
        }

    }

    fun handleInput(path: SourcePath): Single<Path>
}

class CachedCVInput(private val cache: Cache, private val dataSource: DataSource) : CVInput {
    override fun handleInput(path: SourcePath): Single<Path> {
        return dataSource.sources(path)
            .flatMap { cache.cacheSources(it) }
            .map { it.files() }
    }
}

class PlainCVInput : CVInput {
    override fun handleInput(path: SourcePath): Single<Path> = Single.just(Paths.get(path))
}