package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content

interface ContentStorage<T> {
    //todo return T
    fun search(key: String): ContentStorageItem?

    fun prepareStorageItems(key: String): StorageItems<T>

    fun putSingleItem(key: String, content: Content): ContentStorageItem

    fun searchSingleItem(key: String): ContentStorageItem?

    fun remove(key: String)

    interface ContentStorageItem {
        fun content(): Content
    }

    interface StorageItems<T> {
        fun put(content: Content)

        fun save(): T
    }
}