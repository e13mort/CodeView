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

import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.client.ktor.sources.ContentStorageSourcesUrl
import com.github.e13mort.githuburl.SourcesUrl
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Named

@Module
class KtorSourcesModule {
    @Provides
    fun sources(@Named(DI_KEY_SOURCES_URL_STORAGE) storage: ContentStorage<Path>) : SourcesUrl = ContentStorageSourcesUrl(storage)
}