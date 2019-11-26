package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Single

class LoggedFrontend(private val source: Frontend, private val log: Log) : Frontend {
    override fun generate(classes: CVClasses): Single<StoredObject> {
        return Single.fromCallable { classes }
            .doOnEvent { _, _ -> log.log("cvclasses object is ready to be handled") }
            .flatMap { source.generate(it) }
            .doOnEvent { storedObject, throwable -> run {
                storedObject?.let { log.log("storedobject is ready") }
                throwable?.let { log.log(it) }
            } }
    }
}

fun Frontend.withLogs(log: Log): Frontend {
    return LoggedFrontend(this, log)
}