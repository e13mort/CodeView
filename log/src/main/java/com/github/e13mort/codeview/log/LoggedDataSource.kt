package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import io.reactivex.Observable
import io.reactivex.Single

private class LoggedDataSource(private val dataSource: DataSource, private val log: Log) : DataSource by dataSource {
    override fun sources(path: SourcePath): Single<Sources> {
        return dataSource.sources(path).map { LoggedSources(it, log) }
    }
}

private class LoggedSources(private val sources: Sources, private val log: Log) : Sources by sources {
    override fun sources(): Observable<SourceFile> {
        return sources.sources().doOnSubscribe { log.log("datasource ${name()} requested") }
    }
}

fun DataSource.withLogs(log: Log): DataSource {
    return LoggedDataSource(this, log)
}