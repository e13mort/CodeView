package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path

interface CacheRepository {

    interface TemporarySources {
        fun files() : Path
    }

    fun cacheSources(sources: List<SourceFile>): Single<TemporarySources>

    fun clear()
}