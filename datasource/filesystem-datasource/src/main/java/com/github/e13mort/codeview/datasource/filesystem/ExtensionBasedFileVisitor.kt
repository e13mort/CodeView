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

import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class ExtensionBasedFileVisitor(private val ext: String) : SimpleFileVisitor<Path>() {

    internal val files = ArrayList<Path>()

    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        if (isValid(file)) files.add(file)
        return FileVisitResult.CONTINUE
    }

    private fun isValid(file: Path) = file.toString().endsWith(".$ext")
}