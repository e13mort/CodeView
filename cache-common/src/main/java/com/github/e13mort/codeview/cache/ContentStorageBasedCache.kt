package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Sources
import io.reactivex.Single
import java.nio.file.Path

class ContentStorageBasedCache(private val storage: ContentStorage) : Cache {
    override fun cacheSources(sources: Sources): Single<Cache.TemporarySources> {
        return storage
            .search(sources.name())
            .switchIfEmpty(storage.put(sources.name(), sources.sources()))
            .map { TemporarySourcesAdapter(it) }
    }

    internal class TemporarySourcesAdapter(private val storageItem: ContentStorage.ContentStorageItem) :
        Cache.TemporarySources {
        override fun files(): Path {
            return storageItem.path()
        }
    }

}

