/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package di

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.PlainCVInput
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.datasource.git.di.DaggerGitDataSourceComponent
import com.github.e13mort.codeview.datasource.git.di.GitDataSourceModule
import com.github.e13mort.codeview.datasource.git.di.SourcesUrlModule
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
import javax.inject.Named

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
    fun input(@Named("input-storage") contentStorage: ContentStorage<Path>, sourcesUrl: SourcesUrl, log: Log, dataSource: DataSource): CVInput {
        val (input, tag) = if (sourcesUrl.canParse(launchCommand.sourcesPath)) {
            CachedCVInput(dataSource, contentStorage) to "cached input"
        } else {
            PlainCVInput() to "plain input"
        }
        return input.withLogs(log.withTag(tag))
    }

    @Named("input-storage")
    @Provides
    fun cache(cacheName: CacheName): ContentStorage<Path> {
        return PathContentStorageStorage(
            root.resolve(CONTENT_CACHE_FOLDER_NAME),
            cacheName,
            PathRegistry(root.resolve(CONTENT_CACHE_FOLDER_NAME).resolve(REGISTRY_FILE_NAME))
        )

    }

    @Provides
    fun cacheName(): CacheName {
        return UUIDCacheName()
    }

    @Provides
    @Named("raw-data-source")
    fun dataSource(sourcesUrl: SourcesUrl): DataSource {
        return when (launchCommand.githubClient) {
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
                    .sourcesUrlModule(SourcesUrlModule(sourcesUrl))
                    .gitDataSourceModule(GitDataSourceModule(root.resolve(GIT_CACHE_FOLDER_NAME)))
                    .build()
                    .createDataSource()
            }
        }
    }

    @Provides
    fun loggedDataSource(@Named("raw-data-source") dataSource: DataSource, log: Log) : DataSource {
        return dataSource.withLogs(log.withTag("datasource"))
    }

    private fun githubToken(): String {
        launchCommand.githubKey?.let {
            return it
        }
        throw IllegalStateException("Github key is null")
    }
}