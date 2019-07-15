package com.github.e13mort.codeview.client.ktor.di

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class MemoryCache : DataCache {
    override fun createCacheItem(): DataCache.WritableCacheItem {
        return MemoryCacheItem()
    }

    class MemoryCacheItem : DataCache.WritableCacheItem {
        private val stream = ByteArrayOutputStream()

        override fun copyTo(output: OutputStream) {
            ByteArrayInputStream(stream.toByteArray()).copyTo(output)
        }

        override fun write(): OutputStream {
            return stream
        }
    }

}