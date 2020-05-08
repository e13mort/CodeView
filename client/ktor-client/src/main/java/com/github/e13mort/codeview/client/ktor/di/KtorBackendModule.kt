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

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.cache.CVActualSerialization
import com.github.e13mort.codeview.cache.CachedCVTransformation
import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.client.ktor.AppContext
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Named
import javax.inject.Singleton

@Module
class KtorBackendModule(private val context: AppContext) {

    @Singleton
    @Provides
    @Named(DI_KEY_SOURCE_BACKEND)
    fun backend(log: Log): Backend {
        return JavaBackend().withLogs(log.withTag("JavaBackend"))
    }

    @Provides
    @Singleton
    fun cachedBackend(@Named(DI_KEY_SOURCE_BACKEND) source: Backend, @Named(DI_KEY_BACKEND_STORAGE) contentStorage: ContentStorage<Path>, log: Log) : Backend {
        return CachedCVTransformation(
            source,
            contentStorage,
            CVActualSerialization()
        ).withLogs(log.withTag("CachedBackend"))
    }
}