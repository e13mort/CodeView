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
