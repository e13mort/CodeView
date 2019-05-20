package com.github.e13mort.codeview

class CodeView (
    private val source: DataSource,
    private val frontend: Frontend,
    private val backend: Backend) {

    fun run() {
        frontend.save(backend.transformSourcesToCVClasses(source.sources()))
    }
}
