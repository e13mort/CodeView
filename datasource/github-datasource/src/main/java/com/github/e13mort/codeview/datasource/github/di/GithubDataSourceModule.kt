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

package com.github.e13mort.codeview.datasource.github.di

import com.github.e13mort.codeview.datasource.github.DataSourceConfig
import com.github.e13mort.codeview.datasource.github.GithubDataSource
import com.github.e13mort.codeview.datasource.github.GithubPathPartsTransformation
import com.github.e13mort.githuburl.SourcesUrl
import com.jcabi.github.RtGithub
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
class GithubModule {
    @Provides
    fun github(@GithubToken token: String, config: DataSourceConfig, sourcesUrl: SourcesUrl): GithubDataSource {
        return GithubDataSource(config, RtGithub(token), GithubPathPartsTransformation(sourcesUrl))
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class GithubToken