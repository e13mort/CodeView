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

package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.CVTransformation.TransformOperation.OperationState
import com.github.e13mort.codeview.Content
import io.reactivex.Single

class CachedCVTransformation<INPUT, OUTPUT>(
    private val sourceBackend: CVTransformation<INPUT, OUTPUT>,
    private val storage: KeyValueStorage,
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

    private fun save(transformOperation: CVTransformation.TransformOperation<OUTPUT>): Single<OUTPUT> {
        return transformOperation.transform()
            .doOnSuccess { storage.putSingleItem(transformOperation.description(), serialization.serialize(it)) }
    }

    private fun searchForItem(sourceOperation: CVTransformation.TransformOperation<OUTPUT>) : Single<CacheResult<OUTPUT>> {
        return Single.fromCallable {
            try {
                storage.searchSingleItem(sourceOperation.description())?.let {
                    return@fromCallable createCacheResult(deserialize(it), sourceOperation)
                }
            } catch (e: Exception) {}
            return@fromCallable CacheResult(sourceOperation, false)
        }
    }

    private fun deserialize(it: ContentStorage.ContentStorageItem) =
        serialization.deserialize(it.content())

    private fun createCacheResult(
        classes: OUTPUT,
        operation: CVTransformation.TransformOperation<OUTPUT>
    ) : CacheResult<OUTPUT> {
        return CacheResult(
            PredefinedOutputTransformOperation(
                classes,
                operation
            ),
            true
        )
    }

    private class CacheResult<OUTPUT>(val cachedOperation: CVTransformation.TransformOperation<OUTPUT>, val fromCache: Boolean)

    private data class PredefinedOutputTransformOperation<OUTPUT>(
        private val output: OUTPUT,
        private val operation: CVTransformation.TransformOperation<OUTPUT>
    ) :
        CVTransformation.TransformOperation<OUTPUT> by operation {

        override fun transform(): Single<OUTPUT> = Single.just(output)
    }

    interface CVSerialization<INPUT> {
        fun serialize(input: INPUT): Content

        fun deserialize(content: Content) : INPUT
    }
}