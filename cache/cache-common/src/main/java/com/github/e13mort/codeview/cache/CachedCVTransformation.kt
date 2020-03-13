package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.Content
import io.reactivex.Single
import java.nio.file.Path

class CachedCVTransformation<INPUT, OUTPUT>(
    private val sourceBackend: CVTransformation<INPUT, OUTPUT>,
    private val storage: ContentStorage,
    private val serialization: CVSerialization<OUTPUT>
) : CVTransformation<INPUT, OUTPUT> {

    override fun prepare(source: INPUT): Single<CVTransformation.TransformOperation<OUTPUT>> {
        return sourceBackend.prepare(source)
            .map { CachedTransformOperation(it) }
            .flatMap(this::searchForItem)
            .flatMap(this::handleCacheResult)
    }

    private fun handleCacheResult(cacheResult: CacheResult<OUTPUT>): Single<CVTransformation.TransformOperation<OUTPUT>> {
        val transformOperation = cacheResult.cachedOperation
        return if (cacheResult.fromCache) {
            Single.just(transformOperation)
        } else {
            save(transformOperation).map { transformOperation }
        }
    }

    private fun save(transformOperation: CVTransformation.TransformOperation<OUTPUT>): Single<ContentStorage.ContentStorageItem> {
        return storage.put(
            transformOperation.description(),
            transformOperation
                .transform()
                .map { serialization.serialize(it) }
                .toObservable()
        )
    }

    private fun searchForItem(sourceOperation: CVTransformation.TransformOperation<OUTPUT>) : Single<CacheResult<OUTPUT>> {
        return storage
            .search(sourceOperation.description())
            .map(this::deserialize)
            .map { createCacheResult(it, sourceOperation.description()) }
            .toSingle()
            .onErrorReturn{
                CacheResult(
                    sourceOperation,
                    false
                )
            }
    }

    private fun deserialize(it: ContentStorage.ContentStorageItem) =
        serialization.deserialize(it.path())

    private fun createCacheResult(classes: OUTPUT, description: String) : CacheResult<OUTPUT> {
        return CacheResult(
            DumbTransformOperation(
                classes,
                description
            ),
            true
        )
    }

    private class CacheResult<OUTPUT>(val cachedOperation: CVTransformation.TransformOperation<OUTPUT>, val fromCache: Boolean)

    private data class DumbTransformOperation<OUTPUT>(private val classes: OUTPUT, private val description: String) :
        CVTransformation.TransformOperation<OUTPUT> {

        override fun description(): String = description

        override fun transform(): Single<OUTPUT> = Single.just(classes)
    }

    interface CVSerialization<INPUT> {
        fun serialize(input: INPUT): Content

        fun deserialize(path: Path): INPUT
    }
}