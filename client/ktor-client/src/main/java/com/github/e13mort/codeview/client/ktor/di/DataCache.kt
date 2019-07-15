package com.github.e13mort.codeview.client.ktor.di

import java.io.OutputStream

interface DataCache {
    fun createCacheItem(): WritableCacheItem

    interface CacheItem {
        fun copyTo(output: OutputStream)
    }

    interface WritableCacheItem : CacheItem {
        fun write(): OutputStream
    }
}