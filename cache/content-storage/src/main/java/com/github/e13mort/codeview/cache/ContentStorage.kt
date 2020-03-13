package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.nio.file.Path

interface ContentStorage {
    //FIXME searching by key returns ContentStorageItem with only Path, but previously we put Content in it
    fun search(key: String): Maybe<ContentStorageItem>

    fun <T : Content> put(key: String, content: Observable<T>): Single<ContentStorageItem>

    fun putSingleItem(key: String, content: Content) : ContentStorageItem

    fun searchSingleItem(key: String) : ContentStorageItem?

    interface ContentStorageItem {
        fun path(): Path
    }
}