package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

interface ContentStorage {
    fun search(key: String): Maybe<out ContentStorageItem> //bug here

    fun put(key: String, content: Observable<out Content>): Single<out ContentStorageItem>

    fun putSingleItem(key: String, content: Content): ContentStorageItem

    fun searchSingleItem(key: String): ContentStorageItem?

    fun remove(key: String)

    interface ContentStorageItem {
        fun content(): Content
    }
}