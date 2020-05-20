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

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.client.ktor.AppContext
import com.github.e13mort.codeview.work.AsyncWorkRunner
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Named

@Module
class KtorCacheModule(private val appContext: AppContext) {

    companion object {
        const val registryFileName = "registry.json"
    }

    @Provides
    fun input(@Named(DI_KEY_INPUT_STORAGE) cache: ContentStorage<Path>, dataSource: DataSource) : CVInput {
        return CachedCVInput(dataSource, cache, AsyncWorkRunner())
    }

    @Provides
    @Named(DI_KEY_INPUT_STORAGE)
    fun contentStorage() : ContentStorage<Path> {
        return PathContentStorageStorage(
            appContext.sourceCachePath(),
            UUIDCacheName(),
            PathRegistry(appContext.sourceCachePath().resolve(registryFileName))
        )
    }

    @Provides
    @Named(DI_KEY_BACKEND_STORAGE)
    fun backendStorage() : KeyValueStorage {
        return PathKeyValueStorage(
            appContext.backendCachePath(),
            ConstNameUUIDBasedCacheName(appContext.backendStorageItemName()),
            PathRegistry(appContext.backendCachePath().resolve(registryFileName))
        )
    }

    @Provides
    @Named(DI_KEY_FRONTEND_STORAGE)
    fun frontendStorage() : KeyValueStorage {
        return PathKeyValueStorage(
            appContext.frontendCachePath(),
            ConstNameUUIDBasedCacheName(appContext.frontendStorageItemName()),
            PathRegistry(appContext.frontendCachePath().resolve(registryFileName))
        )
    }

    @Provides
    @Named(DI_KEY_OUTPUT_STORAGE)
    fun outputStorage() : KeyValueStorage {
        return PathKeyValueStorage(
            appContext.outputCachePath(),
            ConstNameUUIDBasedCacheName(appContext.outputStorageItemName()),
            PathRegistry(appContext.outputCachePath().resolve(registryFileName))
        )
    }

    @Provides
    @Named(DI_KEY_SOURCES_URL_STORAGE)
    fun sourcesUrlStorage() : KeyValueStorage {
        return PlainFilesKeyValueStorage(appContext.sourcesUrlCachePath())
    }

    @Provides
    @Named(DI_KEY_BRANCH_META_STORAGE)
    fun branchMetaStorage() : KeyValueStorage {
        return PathKeyValueStorage(
            appContext.branchMetaDirPath(),
            ConstNameUUIDBasedCacheName(appContext.branchHashItemName()),
            PathRegistry(appContext.branchMetaDirPath().resolve(registryFileName))
        )
    }
}