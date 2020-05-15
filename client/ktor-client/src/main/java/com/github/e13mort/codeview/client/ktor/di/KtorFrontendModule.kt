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

import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.cache.CachedCVTransformation
import com.github.e13mort.codeview.cache.KeyValueStorage
import com.github.e13mort.codeview.cache.StoredObjectActualSerialization
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class KtorFrontendModule {

    @Singleton
    @Provides
    @Named(DI_KEY_SOURCE_FRONTEND)
    fun frontend(log: Log): Frontend {
        return PulmFrontend().withLogs(log.withTag("SourceFrontend"))
    }

    @Provides
    @Singleton
    fun cachedBackend(@Named(DI_KEY_SOURCE_FRONTEND) source: Frontend, @Named(DI_KEY_FRONTEND_STORAGE) contentStorage: KeyValueStorage, log: Log) : Frontend {
        return CachedCVTransformation(
            source,
            contentStorage,
            StoredObjectActualSerialization()
        ).withLogs(log.withTag("CachedFrontend"))
    }
}