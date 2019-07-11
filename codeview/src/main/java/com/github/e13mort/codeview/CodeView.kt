package com.github.e13mort.codeview

import javax.inject.Inject

typealias SourcePath = String

class CodeView @Inject constructor (
    private val source: DataSource,
    private val frontend: Frontend,
    private val backend: Backend,
    private val output: Output) {

    fun run(parameters: SourcePath = "") {
        source.sources(parameters).toList()
            .flatMap { backend.transformSourcesToCVClasses(it) }
            .flatMap { frontend.generate(it) }
            .map { it.asString() }
            .flatMapCompletable { output.save(it) }
            .subscribe()
    }
}
