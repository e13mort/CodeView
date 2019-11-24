package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.CVClasses
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

internal class ContentStorageBackendCacheTest {
    private val backend = TrackingBackend()
    private val serialization = mock<ContentStorageBackendCache.CVClassSerialization>()
    private val testStorage = ContentStorageBackendCache(backend, PredefinedStorage(), serialization)

    @BeforeEach
    internal fun setUp() {
        whenever(serialization.content(anyOrNull())).thenReturn(mock())
        whenever(serialization.classes(anyOrNull())).thenReturn(mock())
    }

    @Test
    internal fun `data has been pulled from backend if content storage is empty`() {
        testStorage.transformSourcesToCVClasses(Paths.get("test")).test()
        backend.assertCounter(1)
    }

    @Test
    internal fun `underlying backend is untouched if there is a cached item`() {
        testStorage.transformSourcesToCVClasses(Paths.get("existing")).test()
        backend.assertCounter(0)
    }

    @Test
    internal fun `data has been pulled from backend if there is an error occurred during cache reading operation`() {
        whenever(serialization.classes(anyOrNull())).doAnswer { throw Exception() }
        testStorage.transformSourcesToCVClasses(Paths.get("existing-error")).test()
        backend.assertCounter(1)
    }

    internal class PredefinedStorage : ContentStorage {
        override fun search(key: String): Maybe<ContentStorageItem> =
            if (key.contains("existing")) Maybe.just(mock()) else Maybe.empty()

        override fun <T : Content> put(key: String, content: Observable<T>): Single<ContentStorageItem> {
            return content.map { mock<ContentStorageItem>() }.lastOrError()
        }
    }

    internal class TrackingBackend : Backend {
        private var counter = 0

        override fun transformSourcesToCVClasses(path: Path): Single<CVClasses> {
            return Single.fromCallable { mock<CVClasses>() }.doOnEvent { _, _ -> counter++ }
        }

        fun assertCounter(expected: Int) {
            Assertions.assertEquals(expected, counter)
        }

    }

}