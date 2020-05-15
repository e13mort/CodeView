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

package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.github.e13mort.codeview.cache.withTimeLimit
import com.github.e13mort.codeview.client.ktor.AppContext
import com.github.e13mort.codeview.client.ktor.sources.ContentStorageSourcesUrl
import com.github.e13mort.codeview.datasource.git.GitDataSource
import com.github.e13mort.codeview.datasource.git.LocalRepositories
import com.github.e13mort.codeview.datasource.git.RemoteRepositories
import com.github.e13mort.codeview.datasource.git.cached
import com.github.e13mort.githuburl.SourcesUrl
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class KtorSourcesModule {
    @Provides
    fun sources(@Named(DI_KEY_SOURCES_URL_STORAGE) storage: KeyValueStorage) : SourcesUrl = ContentStorageSourcesUrl(storage)
}

@Module
class KtorDataSourceModule(private val context: AppContext) {
    @Provides
    fun dataSource(
        remoteRepositories: RemoteRepositories,
        localRemoteRepositories: LocalRepositories,
        sourcesUrl: SourcesUrl,
        @Named(DI_KEY_BRANCH_META_STORAGE)
        contentStorage: KeyValueStorage
    ): DataSource {
        return GitDataSource(remoteRepositories.cached(contentStorage.withTimeLimit(context.branchMetaTTL())), sourcesUrl, localRemoteRepositories)
    }
}