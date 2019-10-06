package com.github.e13mort.codeview

import com.github.e13mort.codeview.cache.Cache
import io.reactivex.Single
import java.nio.file.Path

class CachedCVInput(private val cache: Cache, private val dataSource: DataSource) :
    CVInput {
    override fun handleInput(path: SourcePath): Single<Path> {
        return dataSource.sources(path)
            .flatMap { cache.cacheSources(it) }
            .map { it.files() }
    }
}