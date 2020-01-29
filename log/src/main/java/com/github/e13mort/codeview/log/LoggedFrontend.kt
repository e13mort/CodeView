package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.*
import io.reactivex.Single

class LoggedFrontend(private val source: Frontend, private val log: Log) : Frontend {
    override fun prepare(source: CVTransformation.TransformOperation<CVClasses>): Single<CVTransformation.TransformOperation<StoredObject>> {
        return Single.fromCallable { source }
            .doOnEvent { _, _ -> log.log("cvclasses object is ready to be handled") }
            .flatMap { this.source.prepare(it) }
            .doOnEvent { storedObject, throwable -> run {
                storedObject?.let { log.log("storedobject is ready") }
                throwable?.let { log.log(it) }
            } }
    }
}

private class LoggedFrontendTransformOperation(private val sourceOperation: CVTransformation.TransformOperation<StoredObject>, private val log: Log) : CVTransformation.TransformOperation<StoredObject> by sourceOperation {
    override fun run(): StoredObject {
        log.log("stored object requested")
        return sourceOperation.run()
    }
}

fun Frontend.withLogs(log: Log): Frontend {
    return LoggedFrontend(this, log)
}