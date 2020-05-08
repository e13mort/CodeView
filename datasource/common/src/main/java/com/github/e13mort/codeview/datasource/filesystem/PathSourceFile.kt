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

import com.github.e13mort.codeview.SourceFile
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

class PathSourceFile(private val path: Path): SourceFile {
    override fun fileInfo(): SourceFile.FileInfo {
        return object : SourceFile.FileInfo {
            override fun lastModifiedDate(): Long = 0

        }
    }

    override fun name(): String {
        return path.fileName.toString()
    }

    override fun read(): InputStream {
        return FileInputStream(path.toFile())
    }

}