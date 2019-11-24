package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Content
import io.reactivex.*
import java.nio.file.Path

class ContentStorageBackendCache(
    private val sourceBackend: Backend,
    private val storage: ContentStorage,
    private val serialization: CVClassSerialization
) : Backend {
    override fun transformSourcesToCVClasses(path: Path): Single<CVClasses> {
        return searchForItem(path)
            .onExceptionResumeNext { save(path).subscribeWith(it) }
            .switchIfEmpty(MaybeSource { save(path).subscribeWith(it) })
            .toSingle()
    }

    private fun searchForItem(path: Path): Maybe<CVClasses> {
        return storage
            .search(path.toString())
            .map { serialization.classes(it.path()) }
    }

    private fun save(path: Path): Maybe<CVClasses> {
        return storage.put(path.toString(), readDataFromBackend(path))
            .map { storageItem -> serialization.classes(storageItem.path()) }
            .toMaybe()
    }

    private fun readDataFromBackend(path: Path): Observable<Content> {
        return sourceBackend.transformSourcesToCVClasses(path)
            .map { serialization.content(it) }
            .toObservable()
    }

    interface CVClassSerialization {
        fun content(classes: CVClasses): Content

        fun classes(path: Path): CVClasses
    }
}