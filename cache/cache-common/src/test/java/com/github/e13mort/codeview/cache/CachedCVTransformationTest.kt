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
import com.github.e13mort.codeview.ProxyCVTransformation
import com.google.common.jimfs.Jimfs
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.InputStream

internal class CachedCVTransformationTest {
    private val serialization = mock<CachedCVTransformation.CVSerialization<String>>().apply {
        whenever(serialize(anyOrNull())).thenReturn(TestContent())
        whenever(deserialize(anyOrNull())).thenReturn("mock")
    }

    private val cvTransformation1: CachedCVTransformation<CVTransformation.TransformOperation<String>, String> =
        CachedCVTransformation(
            ProxyCVTransformation(), createTestStorage(), serialization
        )

    @Test
    internal fun `data has been pulled from backend if content storage is empty`() {
        val operation = TestOperation("test")
        cvTransformation1.prepare(operation).test()
        operation.assertCounter(1)
    }

    @Test
    internal fun `underlying backend is untouched if there is a cached item`() {
        val source = TestOperation("existing")
        cvTransformation1.prepare(source).test()
        source.assertCounter(0)
    }

    @Test
    internal fun `data has been pulled from backend if there is an error occurred during cache reading operation`() {
        whenever(serialization.deserialize(anyOrNull())).doAnswer { throw Exception() }
        val source = TestOperation("existing")
        cvTransformation1.prepare(source).test()
        source.assertCounter(1)
    }

    @Test
    internal fun `cached source operation called again if there was an error`() {
        val operation = TestOperation("existing")
        operation.switchSuccessful(OperationState.ERROR)
        cvTransformation1.prepare(operation).test()
        operation.assertCounter(1)
    }

    @Nested
    inner class ChainedCVTransformationTest {

        private val tr1 = TestStringToStringTransformation()
        private val tr2 = TestTransformedStringToStringTransformation()

        private val storage1 = createTestStorage()
        private val storage2 = createTestStorage()

        @Test
        internal fun `cached transformation with empty storage should request it's source transformation one time`() {
            CachedCVTransformation(tr1, createTestStorage(), serialization)
                .prepare("input")
                .flatMap { it.transform() }
                .test()

            assertEquals(1, tr1.counter)
        }

        @Test
        internal fun `two cached transformations with empty storages should request it's source transformation one time`() {
            CachedCVTransformation(tr1, createTestStorage(), serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, createTestStorage(), serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            assertEquals(1, tr1.counter)
            assertEquals(1, tr2.counter)
        }

        @Test
        internal fun `second call of two cached transformations with empty storages should request it's source transformation one time`() {
            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, storage2, serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, storage2, serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            assertEquals(1, tr1.counter)
            assertEquals(1, tr2.counter)
        }

        @Test
        internal fun `first transform operation called twice if there was an error after the first call`() {
            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, storage2, serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            tr1.state = OperationState.ERROR

            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, storage2, serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            assertEquals(2, tr1.counter)
            assertEquals(1, tr2.counter)
        }

        @Test
        internal fun `both source operations are requested second time if first source has changed description`() {
            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, storage2, serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            tr1.description = "tr1_updated"

            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, storage2, serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            assertEquals(2, tr1.counter)
            assertEquals(2, tr2.counter)
        }

        @Test
        internal fun `source operation is requested second time if the source operation has changed description`() {
            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { it.transform() }
                .test()

            tr1.description = "tr1_updated"

            CachedCVTransformation(tr1, storage1, serialization)
                .prepare("input")
                .flatMap { it.transform() }
                .test()

            assertEquals(2, tr1.counter)
        }

        inner class TestStringToStringTransformation : CVTransformation<String, String> {
            var counter = 0

            internal var state : OperationState = OperationState.READY

            internal var description = "tr1"

            override fun prepare(source: String): Single<CVTransformation.TransformOperation<String>> {
                return Single.just(object : CVTransformation.TransformOperation<String> {
                    override fun description(): String = description

                    override fun transform(): Single<String> =
                        Single.just(source + "_transform1").doOnSubscribe { counter++ }

                    override fun state(): OperationState = state
                })
            }
        }

        inner class TestTransformedStringToStringTransformation :
            CVTransformation<CVTransformation.TransformOperation<String>, String> {
            var counter = 0

            override fun prepare(source: CVTransformation.TransformOperation<String>): Single<CVTransformation.TransformOperation<String>> {
                return Single.just(object : CVTransformation.TransformOperation<String> {
                    override fun description(): String = source.description() + "tr2"

                    override fun transform(): Single<String> =
                        source.transform().map { it + "_transform2" }.doOnSubscribe { counter++ }
                })
            }
        }

    }

    internal class TestOperation(private val to: String, private var state: OperationState = OperationState.READY) : CVTransformation.TransformOperation<String> {
        private var counter = 0

        override fun description(): String {
            return to
        }

        override fun transform(): Single<String> {
            return Single.just(to).doOnSubscribe {
                counter++
            }
        }

        override fun state(): OperationState {
            return state
        }

        fun assertCounter(expected: Int) {
            assertEquals(expected, counter)
        }

        fun switchSuccessful(state: OperationState) {
            this.state = state
        }

    }

    private fun createTestStorage(): PathBasedStorage {
        return PathBasedStorage(Jimfs.newFileSystem().getPath("."), UUIDCacheName()).apply {
            putSingleItem("existing", TestContent())
        }
    }

    internal class TestContent : Content {
        override fun read(): InputStream = "test".byteInputStream()
    }
}