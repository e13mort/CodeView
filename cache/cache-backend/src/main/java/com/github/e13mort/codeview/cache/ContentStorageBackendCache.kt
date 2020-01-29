package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.*
import io.reactivex.*
import java.nio.file.Path

class ContentStorageBackendCache(
    private val sourceBackend: Backend,
    private val storage: ContentStorage,
    private val serialization: CVClassSerialization
) : Backend {

    override fun prepare(source: Path): Single<CVTransformation.TransformOperation<CVClasses>> {
        return sourceBackend.prepare(source)
            .flatMap(this::searchForItem)
            .flatMap(this::handleCacheResult)
    }

    private fun handleCacheResult(cacheResult: CacheResult): Single<CVTransformation.TransformOperation<CVClasses>> {
        val transformOperation = cacheResult.cachedOperation
        return if (cacheResult.fromCache) {
            Single.just(transformOperation)
        } else {
            save(transformOperation).map { transformOperation }
        }
    }

    private fun save(transformOperation: CVTransformation.TransformOperation<CVClasses>): Single<ContentStorage.ContentStorageItem> {
        return storage.put(
                transformOperation.description(),
                Observable.fromCallable { serialization.content(transformOperation.run()) })
    }

    private fun searchForItem(sourceOperation: CVTransformation.TransformOperation<CVClasses>) : Single<CacheResult> {
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

    private class CacheResult(val cachedOperation: CVTransformation.TransformOperation<CVClasses>, val fromCache: Boolean)

    private data class DumbTransformOperation(private val classes: CVClasses, private val description: String) : CVTransformation.TransformOperation<CVClasses> {
        override fun run(): CVClasses = classes

        override fun description(): String = description
    }

    interface CVClassSerialization {
        fun content(classes: CVClasses): Content

        fun classes(path: Path): CVClasses
    }
}