/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.CVTransformation.TransformOperation.OperationState
import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.StoredObject
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.github.e13mort.codeview.output.engine.OutputEngine
import io.reactivex.*
import java.io.*

class CachedOutputEngine(private val source: OutputEngine, private val contentStorage: KeyValueStorage) :
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

    private fun readFromCache(data: CVTransformation.TransformOperation<StoredObject>): Maybe<InputStream> {
        return Maybe.create {
            if (data.state() == OperationState.ERROR) {
                it.onComplete()
                return@create
            }
            contentStorage.searchSingleItem(data.description())?.run {
                it.onSuccess(read())
                return@create
            }
            it.onComplete()
        }
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

fun OutputEngine.toCached(contentStorage: KeyValueStorage): OutputEngine {
    return CachedOutputEngine(this, contentStorage)
}