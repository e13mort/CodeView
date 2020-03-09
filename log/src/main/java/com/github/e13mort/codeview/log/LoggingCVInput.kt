package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.SourcePath
import io.reactivex.Single
import java.nio.file.Path

class LoggingCVInput(private val source: CVInput, private val log: Log) : CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return this.source
            .prepare(source)
            .doOnError { log.log(it) }
            .map { LoggedInputOperation(it, log) }
    }
}

internal class LoggedInputOperation(val source: CVTransformation.TransformOperation<Path>, val log: Log) :
    CVTransformation.TransformOperation<Path> by source {
    override fun transform(): Single<Path> {
        return source.transform().doOnEvent { _, _ -> log.log("operation called") }
    }
}

fun CVInput.withLogs(log: Log): CVInput {
    return LoggingCVInput(this, log)
}
