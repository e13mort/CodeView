package di

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.githuburl.GithubUrl
import com.github.e13mort.githuburl.GithubUrlImpl
import dagger.Module
import dagger.Provides
import factory.LaunchCommand
import java.nio.file.FileSystems

@Module
class InputModule(factory: LaunchCommand) : FactoryModule(factory) {

    companion object {
        const val CACHE_DIR = "cv_cache"
    }

    @Provides
    fun githubURL(): GithubUrl {
        return GithubUrlImpl(factory.sourcesPath)
    }

    @Provides
    fun input(cache: Cache, githubUrl: GithubUrl) : CVInput {
        return if (githubUrl.canParse(factory.sourcesPath))
            CachedCVInput(cache, createGithubDataSource())
        else {
            PlainCVInput()
        }
    }

    @Provides
    fun cache(cacheName: CacheName) : Cache {
        val root = FileSystems.getDefault().getPath(System.getProperty("user.home")).resolve(CACHE_DIR)
        return ContentStorageBasedCache(PathBasedStorage(root, "registry.json", cacheName))
    }

    @Provides
    fun cacheName(): CacheName {
        return UUIDCacheName()
    }

    private fun createGithubDataSource(): GithubDataSource {
        val key = factory.githubKey ?: throw IllegalStateException("Github key is null")
        return GithubDataSource(
            GithubDataSource.DataSourceConfig("java", key)
        )
    }
}