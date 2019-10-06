package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Sources
import io.reactivex.Single
import java.nio.file.Path

interface Cache {

    interface TemporarySources {
        fun files(): Path
    }

    fun cacheSources(sources: Sources): Single<TemporarySources>
}