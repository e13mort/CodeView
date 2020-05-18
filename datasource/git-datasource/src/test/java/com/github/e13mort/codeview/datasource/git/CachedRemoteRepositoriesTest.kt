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

import com.github.e13mort.codeview.cache.PathBasedStorage
import com.github.e13mort.codeview.cache.UUIDCacheName
import com.github.e13mort.codeview.datasource.git.fake.FkRemoteRepositories
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.fake.FkPathDescription
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CachedRemoteRepositoriesTest {

    private val remoteRepositories = FkRemoteRepositories().apply {
        add("repo1", "master", "masterHash")
    }
    private val target = remoteRepositories.cached(PathBasedStorage(
        Jimfs.newFileSystem().getPath("."),
        UUIDCacheName()
    ))

    @Test
    internal fun `basic remote hash method call returns valid data`() {
        val description = prepareValidDescription()
        Assertions.assertEquals("masterHash", target.remoteBranchHash(description))
    }

    @Test
    internal fun `cached remote hash returned after source remote hash updated`() {
        val description = prepareValidDescription()
        target.remoteBranchHash(description)
        remoteRepositories.add("repo1", "master", "updatedMasterHash")
        Assertions.assertEquals("masterHash", target.remoteBranchHash(description))
    }

    @Test
    internal fun `remote hash is null if path description has only branch`() {
        Assertions.assertNull(target.remoteBranchHash(prepareOnlyBranchDescription()))
    }

    @Test
    internal fun `remote hash is null if path description has only url`() {
        Assertions.assertNull(target.remoteBranchHash(prepareOnlyUrlDescription()))
    }

    private fun prepareValidDescription(): FkPathDescription {
        return FkPathDescription().apply {
            add(SourcesUrl.PathDescription.Kind.BRANCH, "master")
            add(SourcesUrl.PathDescription.Kind.GIT_URL_HTTPS, "repo1")
        }
    }

    private fun prepareOnlyUrlDescription(): FkPathDescription {
        return FkPathDescription().apply {
            add(SourcesUrl.PathDescription.Kind.GIT_URL_HTTPS, "repo1")
        }
    }

    private fun prepareOnlyBranchDescription(): FkPathDescription {
        return FkPathDescription().apply {
            add(SourcesUrl.PathDescription.Kind.BRANCH, "master")
        }
    }
}