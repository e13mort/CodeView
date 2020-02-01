package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.cache.ContentStorage.ContentStorageItem
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

typealias ClassFrom = Path
typealias ClassTo = Path

internal class ContentStorageBackendCacheTest {
    private val backend =
        TrackingBackend()
    private val serialization = mock<CachedCVTransformation.CVSerialization<ClassTo>>()
    private val testStorage = CachedCVTransformation(
        backend,
        PredefinedStorage(),
        serialization
    )

    @BeforeEach
    internal fun setUp() {
        whenever(serialization.serialize(anyOrNull())).thenReturn(mock())
        whenever(serialization.deserialize(anyOrNull())).thenReturn(mock())
    }

    @Test
    internal fun `data has been pulled from backend if content storage is empty`() {
        testStorage.prepare(Paths.get("test")).test()
        backend.assertCounter(1)
    }

    @Test
    internal fun `underlying backend is untouched if there is a cached item`() {
        testStorage.prepare(Paths.get("existing")).test()
        backend.assertCounter(0)
    }

    @Test
    internal fun `data has been pulled from backend if there is an error occurred during cache reading operation`() {
        whenever(serialization.deserialize(anyOrNull())).doAnswer { throw Exception() }
        testStorage.prepare(Paths.get("existing-error")).test()
        backend.assertCounter(1)
    }

    internal class PredefinedStorage : ContentStorage {
        override fun search(key: String): Maybe<ContentStorageItem> =
            if (key.contains("existing")) Maybe.just(mock()) else Maybe.empty()

        override fun <T : Content> put(key: String, content: Observable<T>): Single<ContentStorageItem> {
            return content.map { mock<ContentStorageItem>() }.lastOrError()
        }

        override fun putSingleItem(key: String, content: Content): ContentStorageItem {
            throw Exception()
        }

        override fun searchSingleItem(key: String): ContentStorageItem? {
            return null
        }
    }

    internal class TrackingBackend : CVTransformation<ClassFrom, ClassTo> {
        private var counter = 0

        override fun prepare(source: ClassFrom): Single<CVTransformation.TransformOperation<ClassTo>> {
            return Single.fromCallable {
                mock<CVTransformation.TransformOperation<ClassTo>>().apply {
                    whenever(description()).thenReturn(source.toString())
                    whenever(run()).thenAnswer {
                        counter++
                        mock()
                    }
                }
            }
        }

        fun assertCounter(expected: Int) {
            Assertions.assertEquals(expected, counter)
        }

    }

}