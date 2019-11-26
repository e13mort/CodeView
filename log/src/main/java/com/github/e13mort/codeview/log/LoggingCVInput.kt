package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.SourcePath
import io.reactivex.Single
import java.nio.file.Path

class LoggingCVInput(private val source: CVInput, private val log: Log) : CVInput {

    override fun handleInput(path: SourcePath): Single<Path> {
        return Single.fromCallable { path }
            .doOnEvent { t1, _ ->
                log.log("path to handle: $t1")
            }.flatMap {
                source.handleInput(it)
            }.doOnEvent { t1, throwable ->
                t1?.let {
                    log.log("result path: $it")
                }
                throwable?.let { log.log(it) }
            }
    }
}

fun CVInput.withLogs(log: Log) : CVInput {
    return LoggingCVInput(this, log)
}
