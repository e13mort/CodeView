package com.github.e13mort.codeview

import io.reactivex.Single
import javax.inject.Inject

typealias SourcePath = String

class CodeView<T> @Inject constructor(
    private val input: CVInput,
    private val frontend: Frontend,
    private val backend: Backend,
    private val output: Output<T>
) {

    fun run(parameters: SourcePath = ""): Single<T> {
        return input.prepare(parameters)
            .flatMap { backend.prepare(it) }
            .flatMap { frontend.prepare(it) }
            .flatMap { output.save(it) }
    }
}
