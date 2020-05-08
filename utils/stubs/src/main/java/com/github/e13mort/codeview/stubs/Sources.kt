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

package com.github.e13mort.codeview.stubs

import com.github.e13mort.codeview.*
import io.reactivex.Single
import java.io.InputStream

class StubSourceFile(private val stream: InputStream) : SourceFile {
    override fun name(): String = "stub.file"

    override fun fileInfo(): SourceFile.FileInfo = SourceFile.FileInfo.EMPTY

    override fun read(): InputStream = stream

}

class StubSources(private val source: SourceFile) : Sources {
    override fun name(): String = "stub"

    override fun sources(): List<SourceFile> = listOf(source)
}

class StubDataSource(private val sources: Sources) : DataSource {
    override fun name(): String {
        return "stub"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.just(sources)
    }
}

class StubContent(private val stringContent: String = "stub content") : Content {
    override fun read(): InputStream = stringContent.byteInputStream()

}