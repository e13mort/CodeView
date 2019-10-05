package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path

interface CVInput {
    fun handleInput(path: SourcePath): Single<Path>
}

class CachedCVInput(private val cache: Cache, private val dataSource: DataSource) : CVInput {
    override fun handleInput(path: SourcePath): Single<Path> {
        return dataSource.sources(path)
            .flatMap { cache.cacheSources(it) }
            .map { it.files() }
    }

}