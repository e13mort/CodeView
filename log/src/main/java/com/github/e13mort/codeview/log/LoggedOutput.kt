package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.*
import io.reactivex.Single

class LoggedOutput<T>(private val source: Output<T>, private val log: Log) : Output<T> {
    override fun save(data: CVTransformation.TransformOperation<StoredObject>): Single<T> {
        return Single.fromCallable { data }
            .doOnEvent { _, _ -> log.log("StoredObject is ready to be handled") }
            .flatMap { source.save(it) }
            .doOnEvent { result, throwable ->
                result?.let { log.log("StoredObject is handled") }
                throwable?.let { log.log(throwable) }
             }
    }
}

fun <T>Output<T>.withLogs(log: Log): Output<T> {
    return LoggedOutput(this, log)
}