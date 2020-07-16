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

import com.github.e13mort.githuburl.GithubUrlImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OutputFilenameResolverTest {
    private val defaultName = "default.name"
    private val resolver = OutputFilenameResolver(GithubUrlImpl(), defaultName)

    @Test
    internal fun `invalid source url resoled to default file name`() {
        assertEquals(defaultName, resolver.resolveFileName("/some/path"))
    }

    @Test
    internal fun `valid source url of a file resoled to correct file name`() {
        assertEquals("file", resolver.resolveFileName("https://github.com/e13mort/CodeView/tree/master/file.java"))
    }

    @Test
    internal fun `valid source url of a package resoled to default file name`() {
        assertEquals(defaultName, resolver.resolveFileName("https://github.com/e13mort/CodeView/tree/master"))
    }
}