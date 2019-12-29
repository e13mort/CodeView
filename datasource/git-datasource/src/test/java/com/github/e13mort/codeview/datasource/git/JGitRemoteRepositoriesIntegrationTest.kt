package com.github.e13mort.codeview.datasource.git

import com.github.e13mort.codeview.datasource.git.JGitRemoteRepositories.RemoteRepositoryStateListener
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.SourcesUrl.PathDescription.Kind
import com.github.e13mort.githuburl.fake.FkSourceUrl
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Tag("git-integration")
internal class JGitRemoteRepositoriesIntegrationTest {

    private lateinit var repositories: JGitRemoteRepositories
    private lateinit var url: SourcesUrl
    private lateinit var path: Path

    companion object {
        //replace INTEGRATION_GIT with some docker based local repository eventually
        const val INTEGRATION_GIT = "https://github.com/e13mort/codeview-git-integration.git"
        const val MASTER_HASH = "8eed7289561d7e59f8222df31947a3b8aa771000"
        const val FAKE_PATH = "integration_repo"
        val FILE_PATHS = listOf<Path>(
            Paths.get("file1.bin"),
            Paths.get("file1.java"),
            Paths.get("file1.kt"),
            Paths.get("file2.java"),
            Paths.get("file2.kt"),
            Paths.get("dir").resolve("file1.bin"),
            Paths.get("dir").resolve("file1.java"),
            Paths.get("dir").resolve("file1.kt"),
            Paths.get("dir").resolve("file2.java"),
            Paths.get("dir").resolve("file2.kt")
        )
    }

    private val listener = mock<RemoteRepositoryStateListener>()

    @BeforeEach
    internal fun setUp() {
        path = Files.createTempDirectory("jgit_test_repository")
        url = FkSourceUrl().apply {
            add(FAKE_PATH, Kind.BRANCH, "master")
            add(FAKE_PATH, Kind.GIT_URL_HTTPS, INTEGRATION_GIT)
        }
        repositories = JGitRemoteRepositories(listener)
    }

    @Test
    internal fun `branch on real repository is valid`() {
        val pathDescription = url.parse("integration_repo")!!
        throw Exception()
        assertThat(repositories.remoteBranchHash(pathDescription)).isEqualTo(MASTER_HASH)
    }

    @Test
    internal fun `CLONE event emitted if there is no previously cloned repo`() {
        repositories.clone(INTEGRATION_GIT, path)
        verify(listener).onEvent(eq(RemoteRepositoryStateListener.Event.CLONE))
    }

    @Test
    internal fun `OPEN event emitted if there is no previously cloned repo`() {
        repositories.clone(INTEGRATION_GIT, path)
        repositories.clone(INTEGRATION_GIT, path)
        verify(listener).onEvent(eq(RemoteRepositoryStateListener.Event.OPEN))
    }

    @Test
    internal fun `CHECK_OUT event emitter after repo checkout`() {
        repositories.clone(INTEGRATION_GIT, path).checkout(MASTER_HASH)
        verify(listener).onEvent(eq(RemoteRepositoryStateListener.Event.CHECK_OUT))
    }

    @Test
    internal fun `test full integration`() {
        val pathDescription = url.parse("integration_repo")
        val hash = repositories.remoteBranchHash(pathDescription!!)
        repositories.clone(pathDescription.readPart(Kind.GIT_URL_HTTPS), path).checkout(hash!!)
        FILE_PATHS.forEach {
            assertThat(path.resolve(it)).exists()
        }
    }

    @Test
    internal fun `invalid clone repo url leads to CloneException`() {
        assertThrows<RemoteRepositories.CloneException> {
            repositories.clone(INTEGRATION_GIT + "junk", path)
        }
    }

    @Test
    internal fun `invalid checkout hash leads to CloneException`() {
        assertThrows<RemoteRepositories.CloneException> {
            repositories.clone(INTEGRATION_GIT, path).checkout("not a real hash")
        }
    }
}