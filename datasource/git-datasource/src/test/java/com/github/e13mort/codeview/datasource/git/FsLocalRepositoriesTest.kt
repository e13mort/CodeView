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

package com.github.e13mort.codeview.datasource.git

import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

internal class FsLocalRepositoriesTest {
    private val root = Jimfs.newFileSystem().getPath("root").createDir()

    @Test
    internal fun `returned path has valid root`() {
        val repositories = FsLocalRepositories(root)
        val path = repositories.search("https://some.git.url/repo.git").createDir()
        assertThat(path).hasParent(root)
    }
}

fun Path.createDir(): Path {
    Files.createDirectory(this)
    return this
}