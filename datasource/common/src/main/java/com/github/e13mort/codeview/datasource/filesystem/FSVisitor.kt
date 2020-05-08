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

import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class FSVisitor {
    fun visitFolder(folder: Path, options: Options): List<Path> {
        return Files
            .walk(folder, options.depth)
            .filter { isValidName(it, options) }
            .toList()
    }

    private fun isValidName(
        it: Path,
        options: Options
    ) = !Files.isDirectory(it) && it.fileName.toString().endsWith(".${options.fileExtension}")

    data class Options(val fileExtension: String, val depth: Int)
}