package com.github.e13mort.codeview

import javax.inject.Inject

class CodeView @Inject constructor (
    private val source: DataSource,
    private val frontend: Frontend,
    private val backend: Backend,
    private val output: Output) {

    fun run() {
        output.save(
            frontend.generate(
                backend.transformSourcesToCVClasses(
                    source.sources()
                )
            ).asString()
        )
    }
}
