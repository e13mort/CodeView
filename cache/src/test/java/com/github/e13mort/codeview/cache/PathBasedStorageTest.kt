package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.SourceFile
import com.google.common.jimfs.Jimfs
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

internal class PathBasedStorageTest {
    private lateinit var memoryFileSystem: FileSystem
    private lateinit var storage: FileStorageBasedCache.FileStorage
    private lateinit var root: Path

    companion object {
        const val REGISTRY_NAME = "reg.json"
    }

    @BeforeEach
    internal fun setUp() {
        memoryFileSystem = Jimfs.newFileSystem()
        root = memoryFileSystem.getPath(".")
        storage = PathBasedStorage(root, REGISTRY_NAME)
    }

    @Test
    internal fun `there's no registry file after creation`() {
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).doesNotExist()
    }

    @Test
    internal fun `registry file created after the first insertion`() {
        storage.put("some-key", MemorySourceFile().asObservable()).test().assertNoErrors()
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).exists()
    }

    @Test
    fun `registry file is not empty`() {
        storage.put("some-key", MemorySourceFile().asObservable()).test().assertNoErrors()
        assertThat(Files.readAllLines(memoryFileSystem.getPath(REGISTRY_NAME))).isNotEmpty
    }

    @Test
    internal fun `there's no items found on empty storage`() {
        storage.search("key").test().assertNoValues()
    }

    @Test
    internal fun `previously created item is found`() {
        storage.put("key", MemorySourceFile().asObservable()).subscribe()
        storage.search("key").test().assertValueCount(1)
    }

    @Test
    internal fun `item is found on a new file storage`() {
        storage.put("key", MemorySourceFile().asObservable()).test().assertNoErrors()
        val newStorage = PathBasedStorage(root, REGISTRY_NAME)
        newStorage.search("key").test().assertValueCount(1)
    }

    @Test
    internal fun `directory created for a cached item`() {
        storage.put("key", MemorySourceFile().asObservable()).test().assertNoErrors()
        storage.search("key").test().assertValue {
            return@assertValue Files.isDirectory(it.path())
        }
    }

    @Test
    internal fun `source file saved to cache`() {
        storage.put("key", MemorySourceFile().asObservable()).test().assertNoErrors()
        storage.search("key").test().assertValue {
            return@assertValue Files.list(it.path()).toArray().size == 1
        }
    }

    @Test
    fun `root folder contains folder with cache`() {
        val item = storage.put("key", MemorySourceFile().asObservable()).blockingGet()
        assertThat(root.resolve(item.path())).exists()
    }

    private class MemorySourceFile : SourceFile {
        override fun read(): InputStream = ByteArrayInputStream(byteArrayOf())

        override fun fileInfo(): SourceFile.FileInfo = SourceFile.FileInfo.EMPTY

        override fun name(): String = "MemorySourceFile"

        fun asObservable(): Observable<SourceFile> = Observable.just(this)
    }
}