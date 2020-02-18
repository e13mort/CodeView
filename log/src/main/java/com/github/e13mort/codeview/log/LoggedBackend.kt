package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.CVTransformation
import io.reactivex.Single
import java.nio.file.Path

class LoggedBackend(private val sourceBackend: Backend, private val log: Log) : Backend {
    override fun prepare(source: CVTransformation.TransformOperation<Path>): Single<CVTransformation.TransformOperation<CVClasses>> {
        return Single.fromCallable { source }
            .doOnEvent { inPath, _ -> log.log("path to handle: ${inPath.description()}") }
            .flatMap { sourceBackend.prepare(source) }
            .doOnEvent { operation, t2 ->
                run {
                    operation?.let { log.log("operation are created successfully") }
                    t2?.let { log.log(it) }
                }
            }
            .map { LoggedTransformOperation(it, log) }
    }
}

private class LoggedTransformOperation(val source: CVTransformation.TransformOperation<CVClasses>, val log: Log) : CVTransformation.TransformOperation<CVClasses> by source {

    override fun transform(): Single<CVClasses> {
        return source.transform().doOnEvent { _, _ -> log.log("classes are handled") }
    }
}

fun Backend.withLogs(log: Log): Backend {
    return LoggedBackend(this, log)
}