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

import com.github.e13mort.codeview.stubs.StubDataSource
import com.github.e13mort.codeview.stubs.StubSourceFile
import com.github.e13mort.codeview.stubs.StubSources
import org.junit.jupiter.api.Test

internal class FilteredDataSourceTest {

    private val dataSource = StubDataSource(StubSources(StubSourceFile()))

    @Test
    internal fun `filtered data source keeps valid item`() {
        dataSource.filteredByFileName("stub.file").sources("test").map { it.sources() }.test().assertValue {
            it.isNotEmpty()
        }
    }

    @Test
    internal fun `filtered data source filters invalid item`() {
        dataSource.filteredByFileName("some.file").sources("test").map { it.sources() }.test().assertValue {
            it.isEmpty()
        }
    }
}