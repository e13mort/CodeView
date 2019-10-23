package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.Sources
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.nio.file.Path

class FileStorageBasedCache(private val storage: FileStorage) : Cache {
    override fun cacheSources(sources: Sources): Single<Cache.TemporarySources> {
        return storage
            .search(sources.name())
            .switchIfEmpty(storage.put(sources.name(), sources.sources()))
            .map { TemporarySourcesAdapter(it) }
    }

    interface FileStorage {
        fun search(key: String): Maybe<FileStorageItem>

        fun put(key: String, sourceFiles: Observable<SourceFile>): Single<FileStorageItem>

        interface FileStorageItem {
            fun path(): Path
        }
    }

    internal class TemporarySourcesAdapter(private val storageItem: FileStorage.FileStorageItem) :
        Cache.TemporarySources {
        override fun files(): Path {
            return storageItem.path()
        }
    }

}

