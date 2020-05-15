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

package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import com.github.e13mort.codeview.cache.CacheName
import com.github.e13mort.codeview.cache.PathBasedStorage
import com.github.e13mort.codeview.output.engine.OutputEngine
import com.github.e13mort.codeview.stubs.StubFrontendTransformOperation
import com.google.common.jimfs.Jimfs
import io.reactivex.Completable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset

internal class CachedOutputEngineTest {

    private lateinit var outputEngine: CachedOutputEngine
    private lateinit var storage: PathBasedStorage
    private val fileSystem = Jimfs.newFileSystem()
    private val root = fileSystem.getPath(".")

    @BeforeEach
    internal fun setUp() {
        storage = PathBasedStorage(root, "reg.json", object : CacheName {
            override fun createFileName(): String = "result.txt"

            override fun createDirName(): String = "dir"
        })
        outputEngine = CachedOutputEngine(FakeEngine(), storage)
    }

    @Test
    internal fun `save first transform operation item requests source engine to run`() {
        val stream = ByteArrayOutputStream()
        outputEngine.saveDataToOutputStream(StubFrontendTransformOperation(), stream).test()
        assertEquals("fake1", stream.toByteArray().toString(Charset.defaultCharset()))
    }

    @Test
    internal fun `save second transform operation item does not requests source engine to run second time`() {
        val stream = ByteArrayOutputStream()
        outputEngine.saveDataToOutputStream(StubFrontendTransformOperation(), stream).test()
        stream.reset()
        outputEngine.saveDataToOutputStream(StubFrontendTransformOperation(), stream).test()
        assertEquals("fake1", stream.toByteArray().toString(Charset.defaultCharset()))
    }

    class FakeEngine : OutputEngine {

        private var writerCounter = 0

        override fun saveDataToOutputStream(
            data: CVTransformation.TransformOperation<StoredObject>,
            outputStream: OutputStream
        ): Completable {
            return Completable.fromAction {
                outputStream.writer().use {
                    it.append("fake${++writerCounter}")
                }
            }
        }
    }


}