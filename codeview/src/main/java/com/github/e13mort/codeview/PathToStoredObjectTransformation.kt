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

package com.github.e13mort.codeview

import io.reactivex.Observable
import io.reactivex.Single
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

class PathToStoredObjectTransformation @Inject constructor() :
    CVTransformation<CVTransformation.TransformOperation<Path>, StoredObject> {
    override fun prepare(source: CVTransformation.TransformOperation<Path>): Single<CVTransformation.TransformOperation<StoredObject>> {
        return Single.fromCallable { PathTransformOperation(source) }
    }

    class PathTransformOperation(private val source: CVTransformation.TransformOperation<Path>) :
        CVTransformation.TransformOperation<StoredObject> {
        override fun description(): String = source.description()

        override fun transform(): Single<StoredObject> {
            return source
                .transform()
                .flatMapObservable { path -> findFile(path) }
                .firstOrError() //gonna be removed eventually
                .onErrorResumeNext {
                    Single.error(PathToSOTransformationException("Failed to transform path to StoredObject instance", it))
                }
        }

        private fun findFile(path: Path): Observable<StoredObject> {
            return Observable.fromIterable(object : Iterable<Path> {
                override fun iterator(): MutableIterator<Path> {
                    if (!Files.isDirectory(path)) throw IllegalArgumentException("$path should be a directory")
                    return Files.list(path).iterator()
                }
            }).map { PathStoredObject(it) }
        }
    }

    class PathStoredObject(private val path: Path) : StoredObject {
        override fun asString(): String {
            return Files.newBufferedReader(path).readText()
        }
    }

    class PathToSOTransformationException(msg: String, source: Throwable) : Exception(msg, source)
}