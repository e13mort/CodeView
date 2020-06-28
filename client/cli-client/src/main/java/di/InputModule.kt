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
import com.github.e13mort.codeview.datasource.git.GitDataSource
import com.github.e13mort.codeview.datasource.git.di.DataSourceRoot
import com.github.e13mort.codeview.datasource.github.GithubDataSource
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
import javax.inject.Provider

@Module
class InputModule {

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
    fun input(cachedCVInput: Provider<CachedCVInput>, plainCVInput: Provider<PlainCVInput>, sourcesUrl: SourcesUrl, log: Log, launchCommand: LaunchCommand): CVInput {
        val (input, tag) = if (sourcesUrl.canParse(launchCommand.sourcesPath)) {
            cachedCVInput.get() to "cached input"
        } else {
            plainCVInput.get() to "plain input"
        }
        return input.withLogs(log.withTag(tag))
    }

    @Provides
    fun cachedInput(@Named("input-storage") contentStorage: ContentStorage<Path>, dataSource: DataSource): CachedCVInput {
        return CachedCVInput(dataSource, contentStorage)
    }

    @Provides
    fun plainInput(): PlainCVInput {
        return PlainCVInput()
    }

    @Named("input-storage")
    @Provides
    fun cache(cacheName: CacheName, root: Path): ContentStorage<Path> {
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
    fun dataSource(
        launchCommand: LaunchCommand,
        githubDataSource: Provider<GithubDataSource>,
        gitDataSource: Provider<GitDataSource>
    ): DataSource {
        return when (launchCommand.githubClient) {
            GithubClient.REST -> githubDataSource.get()
            GithubClient.GIT -> gitDataSource.get()
        }
    }

    @Provides
    @DataSourceRoot
    fun dataSourceRoot(root: Path) : Path {
        return root.resolve(GIT_CACHE_FOLDER_NAME)
    }

    @Provides
    fun loggedDataSource(@Named("raw-data-source") dataSource: DataSource, log: Log) : DataSource {
        return dataSource.withLogs(log.withTag("datasource"))
    }
}