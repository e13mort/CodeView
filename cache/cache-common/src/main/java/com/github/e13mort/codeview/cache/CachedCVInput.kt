package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.*
import io.reactivex.Single
import java.nio.file.Path

class CachedCVInput(
    private val dataSource: DataSource,
    private val storage: ContentStorage
) :
    CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return Single.fromCallable {
            object : CVTransformation.TransformOperation<Path> {
                override fun description(): String {
                    return source
                }

                override fun transform(): Single<Path> {
                    return dataSource.sources(source)
                        .flatMap { cacheSources(it) }
                        .map { it.path() }
                }

                private fun cacheSources(it: Sources): Single<ContentStorage.ContentStorageItem> {
                    return storage
                        .search(it.name())
                        .switchIfEmpty(storage.put(it.name(), it.sources()))
                }

            }
        }
    }
}