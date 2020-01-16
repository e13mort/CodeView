package com.github.e13mort.codeview.datasource.git.di

import com.github.e13mort.codeview.datasource.git.*
import com.github.e13mort.codeview.datasource.git.JGitRemoteRepositories
import com.github.e13mort.githuburl.GithubUrlImpl
import com.github.e13mort.githuburl.SourcesUrl
import dagger.Component
import dagger.Module
import dagger.Provides
import java.nio.file.Path

@Module
class GitDataSourceModule(private val root: Path) {

    @Provides
    fun remoteRepos() : RemoteRepositories {
        return JGitRemoteRepositories()
    }

    @Provides
    fun sourcesUrl() : SourcesUrl {
        return GithubUrlImpl()
    }

    @Provides
    fun localRepos() : LocalRepositories {
        return FsLocalRepositories(root)
    }
}

@Component(modules = [GitDataSourceModule::class])
interface GitDataSourceComponent {
    fun createDataSource() : GitDataSource
}
