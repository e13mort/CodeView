/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import io.reactivex.Single

private class LoggedDataSource(private val dataSource: DataSource, private val log: Log) : DataSource by dataSource {
    override fun sources(path: SourcePath): Single<Sources> {
        return dataSource.sources(path).map { LoggedSources(it, log) }
    }
}

private class LoggedSources(private val sources: Sources, private val log: Log) : Sources by sources {
    override fun sources(): List<SourceFile> {
        log.log("datasource ${name()} requested")
        return sources.sources()
    }
}

fun DataSource.withLogs(log: Log): DataSource {
    return LoggedDataSource(this, log)
}