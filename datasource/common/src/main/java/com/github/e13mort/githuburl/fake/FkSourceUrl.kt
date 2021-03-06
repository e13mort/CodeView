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

package com.github.e13mort.githuburl.fake

import com.github.e13mort.githuburl.SourcesUrl

class FkSourceUrl : SourcesUrl {
    private val pathToDescription = mutableMapOf<String, FkPathDescription>()

    override fun canParse(path: String): Boolean {
        return pathToDescription.containsKey(path)
    }

    override fun parse(path: String): SourcesUrl.PathDescription? {
        return pathToDescription[path]
    }

    fun add(path: String, kind: SourcesUrl.PathDescription.Kind, kindValue: String) {
        var description = pathToDescription[path]
        if (description == null) description = FkPathDescription()
            .apply { pathToDescription[path] = this }
        description.add(kind, kindValue)
    }

}