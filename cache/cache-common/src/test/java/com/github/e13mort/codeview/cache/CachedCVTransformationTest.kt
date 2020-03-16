package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
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

    @Nested
    inner class ChainedCVTransformationTest {

        @Test
        internal fun `cached transformation with empty storage should request it's source transformation one time`() {
            val tr1 = TestStringToStringTransformation()

            CachedCVTransformation(tr1, createTestStorage(), serialization)
                .prepare("input")
                .flatMap { it.transform() }
                .test()

            assertEquals(1, tr1.counter)
        }

        @Test
        internal fun `two cached transformations with empty storages should request it's source transformation one time`() {

            val tr1 = TestStringToStringTransformation()
            val tr2 = TestTransformedStringToStringTransformation()

            CachedCVTransformation(tr1, createTestStorage(), serialization)
                .prepare("input")
                .flatMap { CachedCVTransformation(tr2, createTestStorage(), serialization).prepare(it) }
                .flatMap { it.transform() }
                .test()

            assertEquals(1, tr1.counter)
            assertEquals(1, tr2.counter)
        }

        inner class TestStringToStringTransformation : CVTransformation<String, String> {
            var counter = 0

            override fun prepare(source: String): Single<CVTransformation.TransformOperation<String>> {
                return Single.just(object : CVTransformation.TransformOperation<String> {
                    override fun description(): String = "tr1"

                    override fun transform(): Single<String> =
                        Single.just(source + "_transform1").doOnSubscribe { counter++ }
                })
            }
        }

        inner class TestTransformedStringToStringTransformation :
            CVTransformation<CVTransformation.TransformOperation<String>, String> {
            var counter = 0

            override fun prepare(source: CVTransformation.TransformOperation<String>): Single<CVTransformation.TransformOperation<String>> {
                return Single.just(object : CVTransformation.TransformOperation<String> {
                    override fun description(): String = "tr2"

                    override fun transform(): Single<String> =
                        source.transform().map { it + "_transform2" }.doOnSubscribe { counter++ }
                })
            }
        }

    }

    internal class TestOperation(private val to: String) : CVTransformation.TransformOperation<String> {
        private var counter = 0

        override fun description(): String {
            return to
        }

        override fun transform(): Single<String> {
            return Single.just(to).doOnSubscribe {
                counter++
            }
        }

        fun assertCounter(expected: Int) {
            assertEquals(expected, counter)
        }

    }

    private fun createTestStorage(): PathBasedStorage {
        return PathBasedStorage(Jimfs.newFileSystem().getPath("."), cacheName = UUIDCacheName()).apply {
            putSingleItem("existing", TestContent())
        }
    }

    internal class TestContent : Content {
        override fun read(): InputStream = "test".byteInputStream()
    }
}