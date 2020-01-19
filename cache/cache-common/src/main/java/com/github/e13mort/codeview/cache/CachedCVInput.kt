package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
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