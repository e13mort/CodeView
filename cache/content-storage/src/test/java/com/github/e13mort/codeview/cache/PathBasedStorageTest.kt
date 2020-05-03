package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import com.google.common.jimfs.Jimfs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

class PathBasedStorageTest {
    private lateinit var memoryFileSystem: FileSystem
    private lateinit var storage: PathBasedStorage
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
        putItems("some-key", MemoryContent())
        assertThat(memoryFileSystem.getPath(REGISTRY_NAME)).exists()
    }

    @Test
    fun `registry file is not empty`() {
        putItems("some-key", MemoryContent())
        assertThat(Files.readAllLines(memoryFileSystem.getPath(REGISTRY_NAME))).isNotEmpty
    }

    @Test
    internal fun `there's no items found on empty storage`() {
        assertNull(storage.search("key"))
    }

    @Test
    internal fun `previously created item is found`() {
        putItems("key", MemoryContent())
        assertNotNull(storage.search("key"))
    }

    @Test
    internal fun `item is found on a new file storage`() {
        putItems("key", MemoryContent())
        val newStorage = PathBasedStorage(root, REGISTRY_NAME, cacheName)
        val key = "key"
        assertNotNull(newStorage.search(key))
    }

    @Test
    internal fun `directory created for a cached item`() {
        putItems("key", MemoryContent())
        assertTrue(Files.isDirectory(storage.search("key")!!.path()))
    }

    @Test
    internal fun `source file saved to cache`() {
        putItems("key", MemoryContent())
        assertEquals(1, Files.list(storage.search("key")!!.path()).toArray().size)
    }

    @Test
    fun `root folder contains folder with cache`() {
        val item = putItems("key", MemoryContent())
        assertThat(root.resolve(item)).exists()
    }

    @Test
    internal fun `two files are saved without errors`() {
        putItems("key", MemoryContent(), MemoryContent())
    }

    @Test
    internal fun `two files are exist in cache`() {
        val item = putItems("key", MemoryContent(), MemoryContent())
        assertEquals(2, Files.list(item).count())
    }

    @Test
    internal fun `cached item is equal to the original item`() {
        putItems("key", "hello".asContent())
        assertEquals("hello", storage.search("key")!!.content().read().reader().readText())
    }

    @Test
    internal fun `search returns empty result on not existing cache dir`() {
        val newStorage = PathBasedStorage(root.resolve("not_existing_dir"), REGISTRY_NAME, cacheName)
        assertNull(newStorage.search("key"))
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

    @Test
    internal fun `removing single item leads to empty result`() {
        storage.putSingleItem("test", "hello".asContent())
        storage.remove("test")
        assertNull(storage.searchSingleItem("test"))
    }

    @Test
    internal fun `removing multiple items leads to empty result`() {
        putItems("key", MemoryContent())
        storage.remove("key")
        assertNull(storage.search("key"))
    }

    @Test
    internal fun `removing single item leads to actual files removal`() {
        storage.putSingleItem("test", "hello".asContent())
        storage.remove("test")
        assertEquals(0L, root.internalDirsCount())
    }

    @Test
    internal fun `removing multiple items leads actual files removal`() {
        putItems("key", MemoryContent())
        storage.remove("key")
        assertEquals(0L, root.internalDirsCount())
    }

    private fun putItems(
        key: String,
        vararg content: Content
    ): Path {
        return storage.prepareStorageItems(key).apply {
            content.forEach {
                put(it)
            }
        }.save()
    }

    internal class MemoryContent(private val bytes : ByteArray = byteArrayOf()) : Content {
        override fun read(): InputStream = ByteArrayInputStream(bytes)
    }
}

private fun String.asContent() : Content {
    return PathBasedStorageTest.MemoryContent(this.toByteArray())
}

private fun Path.internalDirsCount(): Long {
    return Files.list(this).filter { Files.isDirectory(it) }.count()
}