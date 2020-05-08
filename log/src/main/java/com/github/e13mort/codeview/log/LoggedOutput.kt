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