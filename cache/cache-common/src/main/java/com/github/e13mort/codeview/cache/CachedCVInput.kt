package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.CVTransformation.TransformOperation.OperationState
import com.github.e13mort.codeview.work.ImmediateWorkRunner
import com.github.e13mort.codeview.work.WorkRunner
import io.reactivex.*
import java.nio.file.Path

class CachedCVInput(
    private val dataSource: DataSource,
    private val storage: PathBasedStorage,
    private val workRunner: WorkRunner = ImmediateWorkRunner()
) :
    CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return Single.fromCallable {
            object : CVTransformation.TransformOperation<Path> {
                private val sourcesDescription by lazy {
                    dataSource.describeSources(source)
                }

                override fun description(): String {
                    return sourcesDescription
                }

                override fun state(): OperationState {
                    return if (storage.search(sourcesDescription) != null)
                        OperationState.READY
                    else OperationState.ERROR
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
                    val schedule = workRunner.schedule(sources.name()) {
                        val sourcesList = sources.sources()
                        storage.prepareStorageItems(sources.name()).apply {
                            sourcesList.forEach {
                                put(it)
                            }
                            save()
                        }
                    }
                    if (schedule == WorkRunner.NewWorkState.PERFORMED) {
                        storage.search(sources.name())?.apply {
                            return path()
                        }
                    }
                    throw CVTransformation.TransformOperation.LongOperationException()
                }

            }
        }
    }
}