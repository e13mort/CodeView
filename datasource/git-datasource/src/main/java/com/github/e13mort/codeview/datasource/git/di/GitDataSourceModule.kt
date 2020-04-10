package com.github.e13mort.codeview.datasource.git.di

import com.github.e13mort.codeview.datasource.git.*
import com.github.e13mort.codeview.datasource.git.JGitRemoteRepositories
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
    fun localRepos() : LocalRepositories {
        return FsLocalRepositories(root)
    }
}

@Module
class SourcesUrlModule(private val sourcesUrl: SourcesUrl) {
    @Provides
    fun sourcesUrl() = sourcesUrl
}

@Component(modules = [GitDataSourceModule::class, SourcesUrlModule::class])
interface GitDataSourceComponent {
    fun createDataSource() : GitDataSource
}
