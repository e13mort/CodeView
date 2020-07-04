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

package com.github.e13mort.codeview

import com.github.e13mort.codeview.PathToStoredObjectTransformation.PathToSOTransformationException
import com.google.common.jimfs.Jimfs
import io.reactivex.Single
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

internal class PathToStoredObjectTransformationTest {

    private val transformation = PathToStoredObjectTransformation()
    private val root = Jimfs.newFileSystem().getPath(".")
    private val emptyDir = root.resolve("empty")
    private val cacheDir = root.resolve("cache")
    private val rootFile = root.resolve("root.file")

    init {
        Files.createDirectory(emptyDir)
        Files.createDirectory(cacheDir)
        Files.createFile(rootFile)
        Files.createFile(cacheDir.resolve("file1.txt"))
        Files.createFile(cacheDir.resolve("file2.txt"))
    }

    @Test
    internal fun `run transformation on a file should emmit an error`() {
        transform(rootFile).test().assertError(PathToSOTransformationException::class.java)
    }

    @Test
    internal fun `run transformation on an empty dir should emmit an error`() {
        transform(emptyDir).test().assertError(PathToSOTransformationException::class.java)
    }

    @Test
    internal fun `run transformation on a dir with two files should emmit one render object`() {
        transform(cacheDir).test().assertValueCount(1)
    }

    private fun transform(path: Path): Single<StoredObject> {
        return transformation.prepare(path.asTransformOperation()).flatMap { it.transform() }
    }
}