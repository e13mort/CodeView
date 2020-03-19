package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.stubs.StubContent
import com.github.e13mort.codeview.stubs.StubDataSource
import com.github.e13mort.codeview.stubs.StubSourceFile
import com.github.e13mort.codeview.stubs.StubSources
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.io.InputStream

internal class CachedCVInputTest {

    private val root = Jimfs.newFileSystem().getPath(".")
    private val trackedInputStream = TrackedInputStream("fake-content".byteInputStream())

    @Test
    internal fun `empty cached input causes a read operation on source input stream`() {
        createEmptyInput(trackedInputStream)
            .prepare("does-not-matter")
            .flatMap { it.transform() }
            .test()
        assertNotEquals(0, trackedInputStream.readCounter)
    }

    @Test
    internal fun `cached input with cached item does not cause a read operation on source input stream`() {
        createCachedInput(trackedInputStream)
            .prepare("does-not-matter")
            .flatMap { it.transform() }
            .test()
        assertEquals(0, trackedInputStream.readCounter)
    }

    private fun createEmptyInput(inputStream: TrackedInputStream): CachedCVInput {
        return CachedCVInput(
            StubDataSource(StubSources(StubSourceFile(inputStream))),
            PathBasedStorage(root, cacheName = UUIDCacheName())
        )
    }

    private fun createCachedInput(inputStream: TrackedInputStream): CachedCVInput {
        return CachedCVInput(
            StubDataSource(StubSources(StubSourceFile(inputStream))),
            PathBasedStorage(root, cacheName = UUIDCacheName()).apply {
                putSingleItem("stub", StubContent())
            })
    }
}

internal class TrackedInputStream(private val inputStream: InputStream) : InputStream() {
    var readCounter = 0

    override fun read(): Int {
        readCounter++
        return inputStream.read()
    }

}