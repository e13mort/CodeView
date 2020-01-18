package com.github.e13mort.codeview.datasource.github.di

import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.codeview.datasource.github.GithubPathPartsTransformation
import com.github.e13mort.codeview.datasource.github.PathPartsTransformation
import com.github.e13mort.githuburl.SourcesUrl
import com.jcabi.github.Github
import com.jcabi.github.RtGithub
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class GithubDataSourceModule(
    private val token: String,
    private val sourcesUrl: SourcesUrl,
    private val filesExtension: String) {

    @Provides
    fun github(): Github {
        return RtGithub(token)
    }

    @Provides
    fun pathTransformation(): PathPartsTransformation {
        return GithubPathPartsTransformation(sourcesUrl)
    }

    @Provides
    fun config(): GithubDataSource.DataSourceConfig {
        return GithubDataSource.DataSourceConfig(filesExtension)
    }
}

@Component(modules = [GithubDataSourceModule::class])
interface GithubDataSourceComponent {
    fun dataSource(): GithubDataSource
}