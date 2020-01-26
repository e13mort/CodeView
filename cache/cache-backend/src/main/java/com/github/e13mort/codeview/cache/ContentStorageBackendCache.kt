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

    override fun prepareTransformOperation(path: Path): Single<Backend.TransformOperation> {
        return sourceBackend.prepareTransformOperation(path)
            .flatMap(this::searchForItem)
            .flatMap(this::handleCacheResult)
    }

    private fun handleCacheResult(cacheResult: CacheResult): Single<Backend.TransformOperation> {
        val transformOperation = cacheResult.cachedOperation
        return if (cacheResult.fromCache) {
            Single.just(transformOperation)
        } else {
            save(transformOperation).map { transformOperation }
        }
    }

    private fun save(transformOperation: Backend.TransformOperation): Single<ContentStorage.ContentStorageItem> {
        return storage.put(
                transformOperation.description(),
                Observable.fromCallable { serialization.content(transformOperation.classes()) })
    }

    private fun searchForItem(sourceOperation: Backend.TransformOperation) : Single<CacheResult> {
        return storage
            .search(sourceOperation.description())
            .map(this::deserialize)
            .map { createCacheResult(it, sourceOperation.description()) }
            .toSingle()
            .onErrorReturn{ CacheResult(sourceOperation, false) }
    }

    private fun deserialize(it: ContentStorage.ContentStorageItem) =
        serialization.classes(it.path())

    private fun createCacheResult(classes: CVClasses, description: String) : CacheResult {
        return CacheResult(DumbTransformOperation(classes, description), true)
    }

    private class CacheResult(val cachedOperation: Backend.TransformOperation, val fromCache: Boolean)

    private data class DumbTransformOperation(private val classes: CVClasses, private val description: String) : Backend.TransformOperation {
        override fun classes(): CVClasses = classes

        override fun description(): String = description
    }

    interface CVClassSerialization {
        fun content(classes: CVClasses): Content

        fun classes(path: Path): CVClasses
    }
}