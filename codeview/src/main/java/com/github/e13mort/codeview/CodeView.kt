package com.github.e13mort.codeview

import io.reactivex.Single
import javax.inject.Inject

typealias SourcePath = String

class CodeView<T> @Inject constructor (
    private val source: DataSource,
    private val frontend: Frontend,
    private val backend: Backend,
    private val cacheRepository: CacheRepository,
    private val output: Output<T>) {

    fun run(parameters: SourcePath = ""): Single<T> {
        return source.sources(parameters).toList()
            .flatMap { cacheRepository.cacheSources(it) }
            .flatMap { backend.transformSourcesToCVClasses(it.files()) }
            .flatMap { frontend.generate(it) }
            .map { it.asString() }
            .flatMap { output.save(it) }
            .doFinally { cacheRepository.clear() }
    }
}
