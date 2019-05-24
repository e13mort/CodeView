package com.github.e13mort.codeview

class CodeView (
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
