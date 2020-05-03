package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.CVTransformation.TransformOperation.OperationState
import com.github.e13mort.codeview.Content
import io.reactivex.Single

class CachedCVTransformation<INPUT, OUTPUT>(
    private val sourceBackend: CVTransformation<INPUT, OUTPUT>,
    private val storage: ContentStorage<out Any>,
    private val serialization: CVSerialization<OUTPUT>
) : CVTransformation<INPUT, OUTPUT> {

    override fun prepare(source: INPUT): Single<CVTransformation.TransformOperation<OUTPUT>> {
        return sourceBackend.prepare(source)
            .map { CachedTransformOperation(it) }
            .doOnSuccess { validateOperation(it) }
            .flatMap(this::searchForItem)
            .flatMap(this::handleCacheResult)
    }

    private fun validateOperation(operation: CachedTransformOperation<OUTPUT>) {
        if (operation.state() != OperationState.READY) {
            storage.remove(operation.description())
        }
    }

    private fun handleCacheResult(cacheResult: CacheResult<OUTPUT>): Single<CVTransformation.TransformOperation<OUTPUT>> {
        val transformOperation = cacheResult.cachedOperation
        return if (cacheResult.fromCache) {
            Single.just(transformOperation)
        } else {
            save(transformOperation).map { transformOperation }
        }
    }

    private fun save(transformOperation: CVTransformation.TransformOperation<OUTPUT>): Single<out ContentStorage.ContentStorageItem> {
        return transformOperation.transform()
            .map { serialization.serialize(it) }
            .map { storage.putSingleItem(transformOperation.description(), it) }
    }

    private fun searchForItem(sourceOperation: CVTransformation.TransformOperation<OUTPUT>) : Single<CacheResult<OUTPUT>> {
        return Single.fromCallable {
            try {
                storage.search(sourceOperation.description())?.let {
                    return@fromCallable createCacheResult(deserialize(it), sourceOperation.description())
                }
            } catch (e: Exception) {}
            return@fromCallable CacheResult(sourceOperation, false)
        }
    }

    private fun deserialize(it: ContentStorage.ContentStorageItem) =
        serialization.deserialize(it.content())

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

        fun deserialize(content: Content) : INPUT
    }
}