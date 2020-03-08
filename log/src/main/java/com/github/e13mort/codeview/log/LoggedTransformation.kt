package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVTransformation
import io.reactivex.Single

class LoggedTransformation<T, U>(private val cvTransformation: CVTransformation<CVTransformation.TransformOperation<T>, U>, private val log: Log) :
    CVTransformation<CVTransformation.TransformOperation<T>, U> {
    override fun prepare(source: CVTransformation.TransformOperation<T>): Single<CVTransformation.TransformOperation<U>> {
        return Single.fromCallable { source }
            .flatMap { cvTransformation.prepare(it) }
            .doOnError { log.log(SourceTransformationException(it)) }
            .map { LoggedTransformationOperation(it, log) }
    }

    private class LoggedTransformationOperation<U>(val source: CVTransformation.TransformOperation<U>, val log: Log) : CVTransformation.TransformOperation<U> by source {

        override fun transform(): Single<U> {
            return source.transform()
                .doOnEvent { res, throwable -> run {
                    res?.let { log.log("operation called") }
                    throwable?.let { log.log(SourceOperationException(it)) }
                } }
        }
    }
}

sealed class LoggedTransformationException(message: String?, cause: Throwable?) : Exception(message, cause)

class SourceTransformationException(cause: Throwable?) : LoggedTransformationException("Source transformation failed", cause)

class SourceOperationException(cause: Throwable?) : LoggedTransformationException("Source operation failed", cause)

fun <FROM, TO>CVTransformation<CVTransformation.TransformOperation<FROM>, TO>.withLogs(log: Log): CVTransformation<CVTransformation.TransformOperation<FROM>, TO> {
    return LoggedTransformation(this, log)
}