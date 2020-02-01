package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import com.google.common.jimfs.Jimfs
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

internal class PathBasedStorageTest {
    private lateinit var memoryFileSystem: FileSystem
    private lateinit var storage: ContentStorage
    private lateinit var root: Path
    private val cacheName = UUIDCacheName()

    companion object {
        const val REGISTRY_NAME = "reg.json"
    }

    @BeforeEach
    internal fun setUp() {
        memoryFileSystem = Jimfs.newFileSystem()
        root = memoryFileSystem.getPath(".")
        storage = PathBasedStorage(root, REGISTRY_NAME, cacheName)
    }

    @Test
    internal fun `there's no registry file after creation`() {
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).doesNotExist()
    }

    @Test
    internal fun `registry file created after the first insertion`() {
        storage.put("some-key", MemoryContent().asObservable()).test().assertNoErrors()
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).exists()
    }

    @Test
    fun `registry file is not empty`() {
        storage.put("some-key", MemoryContent().asObservable()).test().assertNoErrors()
        assertThat(Files.readAllLines(memoryFileSystem.getPath(REGISTRY_NAME))).isNotEmpty
    }

    @Test
    internal fun `there's no items found on empty storage`() {
        storage.search("key").test().assertNoValues()
    }

    @Test
    internal fun `previously created item is found`() {
        storage.put("key", MemoryContent().asObservable()).subscribe()
        storage.search("key").test().assertValueCount(1)
    }

    @Test
    internal fun `item is found on a new file storage`() {
        storage.put("key", MemoryContent().asObservable()).test().assertNoErrors()
        val newStorage = PathBasedStorage(root, REGISTRY_NAME, cacheName)
        newStorage.search("key").test().assertValueCount(1)
    }

    @Test
    internal fun `directory created for a cached item`() {
        storage.put("key", MemoryContent().asObservable()).test().assertNoErrors()
        storage.search("key").test().assertValue {
            return@assertValue Files.isDirectory(it.path())
        }
    }

    @Test
    internal fun `source file saved to cache`() {
        storage.put("key", MemoryContent().asObservable()).test().assertNoErrors()
        storage.search("key").test().assertValue {
            return@assertValue Files.list(it.path()).toArray().size == 1
        }
    }

    @Test
    fun `root folder contains folder with cache`() {
        val item = storage.put("key", MemoryContent().asObservable()).blockingGet()
        assertThat(root.resolve(item.path())).exists()
    }

    @Test
    internal fun `two files are saved without errors`() {
        val test = storage.put("key", Observable.just(MemoryContent(), MemoryContent())).test()
        test.assertNoErrors().assertComplete()
    }

    @Test
    internal fun `two files are exist in cache`() {
        val item = storage.put("key", Observable.just(MemoryContent(), MemoryContent())).blockingGet()
        assertEquals(2, Files.list(item.path()).count())
    }

    @Test
    internal fun `storage creates cache dir if one doesn't exists`() {
        val newStorage = PathBasedStorage(root.resolve("not_existing_dir"), REGISTRY_NAME, cacheName)
        newStorage.put("key", MemoryContent().asObservable()).test().assertComplete().assertNoErrors()
    }

    @Test
    internal fun `search returns empty result on not existing cache dir`() {
        val newStorage = PathBasedStorage(root.resolve("not_existing_dir"), REGISTRY_NAME, cacheName)
        newStorage.search("key").test().assertComplete().assertNoErrors().assertNoValues()
    }

    @Test
    internal fun `put single content returns path to the target file`() {
        storage.putSingleItem("key", "hello".asContent()).apply {
            assertEquals("hello", Files.readAllLines(path())[0])
        }
    }

    @Test
    internal fun `search for a single not existing value returns null result`() {
        assertNull(storage.searchSingleItem("key"))
    }

    @Test
    internal fun `search for a single existing value returns not null result`() {
        storage.putSingleItem("key", "hello".asContent())
        assertNotNull(storage.searchSingleItem("key"))
    }

    @Test
    internal fun `search for a single existing value returns a valid result`() {
        storage.putSingleItem("key", "hello".asContent())
        storage.searchSingleItem("key")!!.apply {
            assertEquals("hello", Files.readAllLines(path())[0])
        }
    }

    internal class MemoryContent(private val bytes : ByteArray = byteArrayOf()) : Content {
        override fun read(): InputStream = ByteArrayInputStream(bytes)

        fun asObservable(): Observable<Content> = Observable.just(this)
    }
}

private fun String.asContent() : Content {
    return PathBasedStorageTest.MemoryContent(this.toByteArray())
}