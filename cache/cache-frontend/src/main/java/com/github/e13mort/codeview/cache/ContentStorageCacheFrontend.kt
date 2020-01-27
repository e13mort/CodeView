package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.*
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

//todo combine both frontend and backend entities into a single entity
class ContentStorageCacheFrontend(
    private val sourceFrontend: Frontend,
    private val storage: ContentStorage,
    private val serialization: StoredObjectSerialization
) : Frontend {
    override fun prepareTransformOperation(transformOperation: Backend.TransformOperation): Single<Frontend.TransformOperation> {
        return sourceFrontend.prepareTransformOperation(transformOperation)
            .flatMap (this::searchForItem)
            .flatMap (this::handleCacheResult)
    }

    private fun handleCacheResult(cacheResult: CacheResult): Single<Frontend.TransformOperation> {
        val transformOperation = cacheResult.cachedOperation
        return if (cacheResult.fromCache) {
            Single.just(transformOperation)
        } else {
            save(transformOperation).map { transformOperation }
        }
    }

    private fun save(transformOperation: Frontend.TransformOperation): Single<ContentStorage.ContentStorageItem> {
        return storage.put(
            transformOperation.description(),
            Observable.fromCallable { serialization.content(transformOperation.storedObject()) })
    }

    private fun searchForItem(sourceOperation: Frontend.TransformOperation) : Single<CacheResult> {
        return storage
            .search(sourceOperation.description())
            .map(this::deserialize)
            .map { createCacheResult(it, sourceOperation.description()) }
            .toSingle()
            .onErrorReturn{ CacheResult(sourceOperation, false) }
    }

    private fun deserialize(it: ContentStorage.ContentStorageItem) =
        serialization.storedObject(it.path())

    private fun createCacheResult(classes: StoredObject, description: String) : CacheResult {
        return CacheResult(DumbTransformOperation(classes, description), true)
    }

    private class CacheResult(val cachedOperation: Frontend.TransformOperation, val fromCache: Boolean)

    private data class DumbTransformOperation(private val storedObject: StoredObject, private val description: String) : Frontend.TransformOperation {
        override fun storedObject(): StoredObject = storedObject

        override fun description(): String = description
    }

    interface StoredObjectSerialization {
        fun content(storedObject: StoredObject): Content

        fun storedObject(path: Path): StoredObject
    }

    class StoredObjectActualSerialization : StoredObjectSerialization {
        override fun content(storedObject: StoredObject): Content {
            return object : Content {
                override fun read(): InputStream {
                    return ByteArrayInputStream(storedObject.asString().toByteArray(charset = Charset.forName("UTF-8")))
                }
            }
        }

        override fun storedObject(path: Path): StoredObject {
            return object : StoredObject {
                override fun asString(): String {
                    return Files.readAllBytes(path).toString(charset = Charset.forName("UTF-8"))
                }
            }

        }

    }
}