package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.cache.ContentStorage
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class LoggedContentStorage(private val source: ContentStorage, private val log: Log) : ContentStorage by source {

    override fun search(key: String): Maybe<out ContentStorage.ContentStorageItem> {
        return source.search(key)
            .doOnSuccess { log.log("item found for key $key") }
            .doOnComplete { log.log("item isn't found for key $key") }
    }

    override fun put(key: String, content: Observable<out Content>): Single<out ContentStorage.ContentStorageItem> {
        return source.put(key, content)
            .doOnEvent { contentStorageItem, error ->
                contentStorageItem?.let { log.log("items saved with key $key") }
                error?.let { log.log(error) }
            }
    }
}

fun ContentStorage.withLogs(log: Log) : ContentStorage = LoggedContentStorage(this, log)