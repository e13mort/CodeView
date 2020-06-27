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

package com.github.e13mort.codeview.datasource.git.di

import com.github.e13mort.codeview.datasource.git.*
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Qualifier

@Module
class GitDataSourceModule {

    @Provides
    fun remoteRepos() : RemoteRepositories {
        return JGitRemoteRepositories()
    }

    @Provides
    fun localRepos(@DataSourceRoot root: Path) : LocalRepositories {
        return FsLocalRepositories(root)
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DataSourceRoot
