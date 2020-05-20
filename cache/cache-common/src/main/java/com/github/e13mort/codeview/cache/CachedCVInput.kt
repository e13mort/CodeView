/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.CVTransformation.TransformOperation.OperationState
import com.github.e13mort.codeview.work.ImmediateWorkRunner
import com.github.e13mort.codeview.work.WorkRunner
import io.reactivex.*
import java.nio.file.Path

class CachedCVInput(
    private val dataSource: DataSource,
    private val storage: PathContentStorageStorage,
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
                                return@map typedContent()
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
                            return typedContent()
                        }
                    }
                    throw CVTransformation.TransformOperation.LongOperationException()
                }

            }
        }
    }
}