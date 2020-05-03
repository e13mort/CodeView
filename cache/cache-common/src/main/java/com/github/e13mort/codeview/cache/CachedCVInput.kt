package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.*
import io.reactivex.*
import java.nio.file.Path

class CachedCVInput(
    private val dataSource: DataSource,
    private val storage: PathBasedStorage
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
                        .map {
                            storage.search(it.name())?.apply {
                                return@map path()
                            }
                            return@map saveToCache(it)
                        }

                }

                private fun saveToCache(sources: Sources): Path {
                    storage.prepareStorageItems(sources.name()).let { items ->
                        sources.sources().forEach {
                            items.put(it)
                        }
                        return items.save()
                    }
                }

            }
        }
    }
}