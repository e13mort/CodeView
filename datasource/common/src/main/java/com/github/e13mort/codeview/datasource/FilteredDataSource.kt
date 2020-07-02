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

package com.github.e13mort.codeview.datasource

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourceFile
import com.github.e13mort.codeview.SourcePath
import com.github.e13mort.codeview.Sources
import io.reactivex.Single
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Filtered

class FilteredDataSource(private val dataSource: DataSource, private val filter: SourceFileFilter) :
    DataSource by dataSource {

    interface SourceFileFilter {
        fun isSourceFileValid(file: SourceFile): Boolean

        operator fun invoke(sourceFile: SourceFile): Boolean {
            return isSourceFileValid(sourceFile)
        }
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return dataSource.sources(path).map { FilteredSources(it) }
    }

    internal inner class FilteredSources(private val sources: Sources) : Sources by sources {
        override fun sources(): List<SourceFile> {
            return sources.sources().filter { filter(it) }
        }
    }

}

fun DataSource.filteredByFileName(extension: String): DataSource {
    return FilteredDataSource(
        this,
        FileNameFilter(extension)
    )
}

internal class FileNameFilter(private val fileName: String) :
    FilteredDataSource.SourceFileFilter {
    override fun isSourceFileValid(file: SourceFile): Boolean = file.name() == fileName
}