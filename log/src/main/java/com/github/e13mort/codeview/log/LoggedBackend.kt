package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.CVClasses
import io.reactivex.Single
import java.nio.file.Path

class LoggedBackend(private val source: Backend, private val log: Log) : Backend {
    override fun prepareTransformOperation(path: Path): Single<Backend.TransformOperation> {
        return Single.fromCallable { path }
            .doOnEvent { inPath, _ -> log.log("path to handle: $inPath") }
            .flatMap { source.prepareTransformOperation(path) }
            .doOnEvent { operation, t2 ->
                run {
                    operation?.let { log.log("operation are created successfully") }
                    t2?.let { log.log(it) }
                }
            }
            .map { LoggedTransformOperation(it, log) }
    }
}

private class LoggedTransformOperation(val source: Backend.TransformOperation, val log: Log) : Backend.TransformOperation by source {
    override fun run(): CVClasses {
        log.log("classes are requested")
        return source.run()
    }
}

fun Backend.withLogs(log: Log): Backend {
    return LoggedBackend(this, log)
}