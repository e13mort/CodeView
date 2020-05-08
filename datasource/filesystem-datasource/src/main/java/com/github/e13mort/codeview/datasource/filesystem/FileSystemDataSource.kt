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

package com.github.e13mort.codeview.datasource.filesystem

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.Sources
import io.reactivex.Observable
import io.reactivex.Single
import java.nio.file.*
import java.util.*
import kotlin.collections.ArrayList

class FileSystemDataSource: DataSource {

    companion object {
        const val MAX_DEPTH = 1
    }

    override fun name(): String {
        return "filesystem"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.fromCallable {
            val javaFileVisitor = ExtensionBasedFileVisitor("java")
            Files.walkFileTree(Paths.get(path), EnumSet.noneOf(FileVisitOption::class.java), MAX_DEPTH, javaFileVisitor)

            FileSystemSources(path, javaFileVisitor.files)
        }
    }

    private class FileSystemSources(private val path: SourcePath, private val files: ArrayList<Path>) : Sources {

        override fun name(): String {
            return path
        }

        override fun sources(): List<SourceFile> {
            return files.map { PathSourceFile(it) }
        }
    }

}