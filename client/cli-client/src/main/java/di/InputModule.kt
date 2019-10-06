package di

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.githuburl.GithubUrl
import com.github.e13mort.githuburl.GithubUrlImpl
import dagger.Module
import dagger.Provides
import factory.LaunchCommand

@Module
class InputModule(factory: LaunchCommand) : FactoryModule(factory) {

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
        return TmpDirBasedCache(cacheName, createDirName(cacheName))
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

    private fun createDirName(cacheName: CacheName) = "tmp${cacheName.createDirName()}"
}