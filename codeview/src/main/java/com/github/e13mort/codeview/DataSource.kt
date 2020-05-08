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

import io.reactivex.Single
import java.io.InputStream

interface DataSource {
    object EMPTY : DataSource {
        override fun name(): String = "Empty data source"

        override fun sources(path: SourcePath): Single<Sources> = Single.just(Sources.EMPTY)
    }

    fun name(): String

    fun sources(path: SourcePath): Single<Sources>

    fun describeSources(source: SourcePath): String = "invalid"
}

interface Sources {
    object EMPTY : Sources {
        override fun name(): String = "empty sources"

        override fun sources(): List<SourceFile> = emptyList()
    }

    fun name(): String

    fun sources(): List<SourceFile>
}

interface Content {
    fun read(): InputStream
}

interface SourceFile : Content {
    fun name(): String

    fun fileInfo(): FileInfo

    interface FileInfo {
        companion object EMPTY : FileInfo {
            override fun lastModifiedDate(): Long = 0
        }

        fun lastModifiedDate(): Long
    }
}

fun String.asContent(): Content {
    return object : Content {
        override fun read(): InputStream = this@asContent.toByteArray().inputStream()

    }
}

fun Content.asString(): String {
    return read().reader().readText()
}