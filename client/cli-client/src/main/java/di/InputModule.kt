package di

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.PlainCVInput
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.datasource.git.di.DaggerGitDataSourceComponent
import com.github.e13mort.codeview.datasource.git.di.GitDataSourceModule
import com.github.e13mort.codeview.datasource.github.di.DaggerGithubDataSourceComponent
import com.github.e13mort.codeview.datasource.github.di.GithubDataSourceModule
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import com.github.e13mort.githuburl.GithubUrlImpl
import com.github.e13mort.githuburl.SourcesUrl
import dagger.Module
import dagger.Provides
import factory.LaunchCommand
import factory.LaunchCommand.GithubClient
import java.nio.file.Path

@Module
class InputModule(factory: LaunchCommand, private val root: Path) : FactoryModule(factory) {

    companion object {
        const val REGISTRY_FILE_NAME = "registry.json"
        const val GIT_CACHE_FOLDER_NAME = "git_cache"
        const val CONTENT_CACHE_FOLDER_NAME = "content_cache"
    }

    @Provides
    fun githubURL(): SourcesUrl {
        return GithubUrlImpl()
    }

    @Provides
    fun input(cache: Cache, sourcesUrl: SourcesUrl, log: Log, githubDataSource: DataSource): CVInput {
        val (input, tag) = if (sourcesUrl.canParse(factory.sourcesPath)) {
            CachedCVInput(cache, githubDataSource) to "cached input"
        } else {
            PlainCVInput() to "plain input"
        }
        return input.withLogs(log.withTag(tag))
    }

    @Provides
    fun cache(cacheName: CacheName): Cache {
        return ContentStorageBasedCache(
            PathBasedStorage(
                root.resolve(CONTENT_CACHE_FOLDER_NAME),
                REGISTRY_FILE_NAME,
                cacheName
            )
        )
    }

    @Provides
    fun cacheName(): CacheName {
        return UUIDCacheName()
    }

    @Provides
    fun dataSource(sourcesUrl: SourcesUrl): DataSource {
        return when (factory.githubClient) {
            GithubClient.REST -> {
                DaggerGithubDataSourceComponent
                    .builder()
                    .githubDataSourceModule(GithubDataSourceModule(githubToken(), sourcesUrl, "java"))
                    .build()
                    .dataSource()
            }
            GithubClient.GIT -> {
                DaggerGitDataSourceComponent
                    .builder()
                    .gitDataSourceModule(GitDataSourceModule(root.resolve(GIT_CACHE_FOLDER_NAME)))
                    .build()
                    .createDataSource()
            }
        }
    }

    private fun githubToken(): String {
        factory.githubKey?.let {
            return it
        }
        throw IllegalStateException("Github key is null")
    }
}