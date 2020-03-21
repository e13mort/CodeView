package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.cache.ContentStorage
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Maybe
import org.junit.jupiter.api.Test

internal class LoggedContentStorageTest {

    private val contentStorage : ContentStorage = mock()

    private val log : Log = mock()

    @Test
    internal fun `existing item leads to regular log call`() {
        whenever(contentStorage.search(any())).thenReturn(Maybe.just(mock()))
        LoggedContentStorage(contentStorage, log).search("key").test()
        verify(log).log(eq("item found for key key"))
    }

    @Test
    internal fun `non existing item leads to error log call`() {
        whenever(contentStorage.search(any())).thenReturn(Maybe.empty())
        LoggedContentStorage(contentStorage, log).search("key").test()
        verify(log).log(eq("item isn't found for key key"))
    }
}