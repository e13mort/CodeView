package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.StoredObject
import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.output.engine.OutputEngine
import io.reactivex.*
import java.io.*

class CachedOutputEngine<T>(private val source: OutputEngine, private val contentStorage: ContentStorage<T>) :
    OutputEngine {

    override fun saveDataToOutputStream(
        data: CVTransformation.TransformOperation<StoredObject>,
        outputStream: OutputStream
    ): Completable {

        return readFromCache(data)
            .switchIfEmpty(readFromSource(data))
            .doOnSuccess { copyToOutput(outputStream, it) }
            .ignoreElement()
    }

    private fun readFromCache(data: CVTransformation.TransformOperation<StoredObject>) =
        Maybe.create<InputStream> {
            contentStorage.searchSingleItem(data.description())?.run {
                it.onSuccess(content().read())
                return@create
            }
            it.onComplete()
        }


    private fun copyToOutput(outputStream: OutputStream, inputStream: InputStream) =
        inputStream.copyTo(outputStream)


    private fun readFromSource(data: CVTransformation.TransformOperation<StoredObject>): SingleSource<InputStream> {
        val cachedOutputStream = ByteArrayOutputStream()
        return source.saveDataToOutputStream(data, cachedOutputStream)
            .toSingle { cachedOutputStream.toByteArray() }
            .doOnSuccess { saveToCache(data.description(), it) }
            .map { ByteArrayInputStream(it) }
    }

    private fun saveToCache(description: String, data: ByteArray) {
        contentStorage.putSingleItem(description, MemoryContent(data))
    }

    private class MemoryContent(
        private val data: ByteArray
    ) : Content {

        override fun read(): InputStream {
            return ByteArrayInputStream(data)
        }
    }
}

fun <T>OutputEngine.toCached(contentStorage: ContentStorage<T>): OutputEngine {
    return CachedOutputEngine(this, contentStorage)
}