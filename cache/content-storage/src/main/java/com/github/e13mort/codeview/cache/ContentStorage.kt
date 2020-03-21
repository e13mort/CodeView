package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.nio.file.Path

interface ContentStorage {
    fun search(key: String): Maybe<ContentStorageItem>

    fun put(key: String, content: Observable<out Content>): Single<ContentStorageItem>

    fun putSingleItem(key: String, content: Content) : ContentStorageItem

    fun searchSingleItem(key: String) : ContentStorageItem?

    interface ContentStorageItem {
        fun path(): Path
    }
}