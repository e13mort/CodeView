package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.CVClasses
import io.reactivex.Single
import java.nio.file.Path

class LoggedBackend(private val source: Backend, private val log: Log) : Backend {
    override fun transformSourcesToCVClasses(path: Path): Single<CVClasses> {
        return Single.fromCallable { path }
            .doOnEvent { inPath, _ -> log.log("path to handle: $inPath") }
            .flatMap { source.transformSourcesToCVClasses(path) }
            .doOnEvent { classes, t2 ->
                run {
                    classes?.let { log.log("cvclasses are created successfully") }
                    t2?.let { log.log(it) }
                }
            }
    }

}

fun Backend.withLogs(log: Log): Backend {
    return LoggedBackend(this, log)
}