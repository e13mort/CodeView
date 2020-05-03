package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.cache.ContentStorage

class LoggedContentStorage<T>(private val source: ContentStorage<T>, private val log: Log) : ContentStorage<T> by source {

    override fun search(key: String): ContentStorage.ContentStorageItem? {
        log.log("item found for key $key")
        return source.search(key).also {
            log.log("item isn't found for key $key")
        }

    }
}

fun <T>ContentStorage<T>.withLogs(log: Log) : ContentStorage<T> = LoggedContentStorage(this, log)