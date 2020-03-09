package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.*
import io.reactivex.Single

class LoggedOutput<T>(private val source: Output<T>, private val log: Log) : Output<T> {
    override fun save(data: CVTransformation.TransformOperation<StoredObject>): Single<T> {
        return source
            .save(LoggedTransformationOperation(data, log))
    }
}

fun <T> Output<T>.withLogs(log: Log): Output<T> {
    return LoggedOutput(this, log)
}