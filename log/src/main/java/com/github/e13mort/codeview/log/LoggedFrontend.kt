package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Single

class LoggedFrontend(private val source: Frontend, private val log: Log) : Frontend {
    override fun prepareTransformOperation(transformOperation: Backend.TransformOperation): Single<Frontend.TransformOperation> {
        return Single.fromCallable { transformOperation }
            .doOnEvent { _, _ -> log.log("cvclasses object is ready to be handled") }
            .flatMap { source.prepareTransformOperation(it) }
            .doOnEvent { storedObject, throwable -> run {
                storedObject?.let { log.log("storedobject is ready") }
                throwable?.let { log.log(it) }
            } }
    }
}

private class LoggedFrontendTransformOperation(private val sourceOperation: Frontend.TransformOperation, private val log: Log) : Frontend.TransformOperation by sourceOperation {
    override fun run(): StoredObject {
        log.log("stored object requested")
        return sourceOperation.run()
    }
}

fun Frontend.withLogs(log: Log): Frontend {
    return LoggedFrontend(this, log)
}