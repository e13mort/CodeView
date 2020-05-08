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