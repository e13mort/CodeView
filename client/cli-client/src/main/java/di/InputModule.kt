package di

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.PlainCVInput
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.codeview.datasource.github.GithubPathPartsTransformation
import com.github.e13mort.codeview.datasource.github.PathPartsTransformation
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import com.github.e13mort.githuburl.SourcesUrl
import com.github.e13mort.githuburl.GithubUrlImpl
import com.jcabi.github.Github
import com.jcabi.github.RtGithub
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
    fun githubURL(): SourcesUrl {
        return GithubUrlImpl()
    }

    @Provides
    fun input(cache: Cache, sourcesUrl: SourcesUrl, log: Log, githubDataSource: GithubDataSource) : CVInput {
        val (input, tag) = if (sourcesUrl.canParse(factory.sourcesPath)) {
            CachedCVInput(cache, githubDataSource) to "cached input"
        }
        else {
            PlainCVInput() to "plain input"
        }
        return input.withLogs(log.withTag(tag))
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

    @Provides
    fun github(): Github {
        return RtGithub(factory.githubKey ?: throw IllegalStateException("Github key is null"))
    }

    @Provides
    fun githubDataSource(github: Github, partsTransformation: PathPartsTransformation): GithubDataSource {
        return GithubDataSource(
            GithubDataSource.DataSourceConfig("java"),
            github, partsTransformation
        )
    }

    @Provides
    fun pathTransformation(url: SourcesUrl): PathPartsTransformation {
        return GithubPathPartsTransformation(url)
    }
}