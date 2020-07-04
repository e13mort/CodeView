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

package com.github.e13mort.codeview.client.cli

import com.github.e13mort.githuburl.SourcesUrl
import javax.inject.Inject

internal const val PUML_EXTENSION = ".puml"

class CodeViewTypeResolver @Inject constructor(private val sourcesUrl: SourcesUrl) {
    enum class CodeViewType { CLASSES, PUML }

    fun resolve(path: String): CodeViewType {
        sourcesUrl.parse(path)?.let {
            if (it.hasPart(SourcesUrl.PathDescription.Kind.FILE_NAME)) {
                if (it.readPart(SourcesUrl.PathDescription.Kind.FILE_NAME).endsWith(PUML_EXTENSION)) {
                    return CodeViewType.PUML
                }
            }
        }
        return CodeViewType.CLASSES
    }
}