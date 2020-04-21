package sources

import com.github.e13mort.codeview.cache.ConstNameUUIDBasedCacheName
import com.github.e13mort.codeview.cache.PathBasedStorage
import com.github.e13mort.codeview.client.ktor.sources.ContentStorageSourcesUrl
import com.github.e13mort.codeview.stubs.StubContent
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.util.stream.Stream

internal class ContentStorageSourcesUrlTest {

    private val root = Jimfs.newFileSystem().getPath(".")
    private val contentStorage = PathBasedStorage(root, cacheName = ConstNameUUIDBasedCacheName("content.json"))
    private val sources = ContentStorageSourcesUrl(contentStorage)

    @Test
    internal fun `cached item exists`() {
        prepareStorage("data", "{}")
        assertNotNull(sources.parse("data"))
    }

    @Test
    internal fun `empty cached item doesn't contains any parts`() {
        prepareStorage("data", "{}")
        val pathDescription = sources.parse("data")
        assertFalse(pathDescription!!.hasPart(*Kind.values()))
    }

    @ParameterizedTest
    @MethodSource("args")
    internal fun `cached item contains corresponding part`(key: String, value: String, kind: Kind) {
        prepareStorage("data", "{ $key: \"$value\" }")
        val pathDescription = sources.parse("data")
        assertTrue(pathDescription!!.hasPart(kind))
    }

    @ParameterizedTest
    @MethodSource("args")
    internal fun `cached item contains valid value`(key: String, value: String, kind: Kind) {
        prepareStorage("data", "{ $key: \"$value\" }")
        val pathDescription = sources.parse("data")
        assertEquals(value, pathDescription!!.readPart(kind))
    }

    @Test
    internal fun `two items might be cached`() {
        prepareStorage("data", "{ branch: \"master\" }")
        prepareStorage("data2", "{ branch: \"dev\" }")
        assertEquals("master", sources.parse("data")!!.readPart(Kind.BRANCH))
        assertEquals("dev", sources.parse("data2")!!.readPart(Kind.BRANCH))
    }

    @Test
    internal fun `invalid item is null`() {
        assertNull(sources.parse("data"))
    }

    @EnumSource(Kind::class)
    @ParameterizedTest
    internal fun `empty item throws exceptions on read attempts`(kind: Kind) {
        prepareStorage("data", "{}")
        val pathDescription = sources.parse("data")
        assertThrows(Exception::class.java) {
            pathDescription!!.readPart(kind)
        }
    }

    companion object{
        @Suppress("unused")
        @JvmStatic
        fun args(): Stream<Arguments> = Stream.of(
            Arguments.of("user_name", "user", Kind.USER_NAME),
            Arguments.of("branch", "master", Kind.BRANCH),
            Arguments.of("path", "some/path", Kind.PATH),
            Arguments.of("project_name", "project", Kind.PROJECT_NAME),
            Arguments.of("git_url_https", "https://some.project.com/repo.git", Kind.GIT_URL_HTTPS)
        )
    }

    private fun prepareStorage(key: String, content: String) {
        contentStorage.putSingleItem(key, StubContent(content))
    }
}