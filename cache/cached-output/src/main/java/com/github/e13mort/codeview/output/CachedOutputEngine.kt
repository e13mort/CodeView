package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.StoredObject
import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.output.engine.OutputEngine
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class CachedOutputEngine(private val source: OutputEngine, private val contentStorage: ContentStorage) :
    OutputEngine {

    private fun copyToOutput(
        it: ContentStorage.ContentStorageItem,
        outputStream: OutputStream
    ): Long {
        return it.content().read().copyTo(outputStream)
    }

    override fun saveDataToOutputStream(
        data: CVTransformation.TransformOperation<StoredObject>,
        outputStream: OutputStream
    ) {
        var storageItem = contentStorage.searchSingleItem(data.description())
        if (storageItem == null) {
            storageItem = contentStorage.putSingleItem(data.description(), MemoryContent(data, source))
        }
        copyToOutput(storageItem, outputStream)

    }

    private class MemoryContent(
        private val data: CVTransformation.TransformOperation<StoredObject>,
        private val source: OutputEngine
    ) : Content {

        override fun read(): InputStream {
            val outputStream = ByteArrayOutputStream()
            source.saveDataToOutputStream(data, outputStream)
            return ByteArrayInputStream(outputStream.toByteArray())
        }
    }
}

fun OutputEngine.toCached(contentStorage: ContentStorage): OutputEngine {
    return CachedOutputEngine(this, contentStorage)
}