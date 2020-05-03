package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.client.ktor.AppContext
import com.github.e13mort.codeview.datasource.git.GitDataSource
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Named

@Module
class KtorCacheModule(private val appContext: AppContext) {

    @Provides
    fun input(@Named(DI_KEY_INPUT_STORAGE) cache: PathBasedStorage, dataSource: GitDataSource) : CVInput {
        return CachedCVInput(dataSource, cache)
    }

    @Provides
    @Named(DI_KEY_INPUT_STORAGE)
    fun contentStorage() : PathBasedStorage {
        return PathBasedStorage(appContext.sourceCachePath(), cacheName = UUIDCacheName())
    }

    @Provides
    @Named(DI_KEY_BACKEND_STORAGE)
    fun backendStorage() : ContentStorage<Path> {
        return PathBasedStorage(appContext.backendCachePath(), cacheName = ConstNameUUIDBasedCacheName(appContext.backendStorageItemName()))
    }

    @Provides
    @Named(DI_KEY_FRONTEND_STORAGE)
    fun frontendStorage() : ContentStorage<Path> {
        return PathBasedStorage(appContext.frontendCachePath(), cacheName = ConstNameUUIDBasedCacheName(appContext.frontendStorageItemName()))
    }

    @Provides
    @Named(DI_KEY_OUTPUT_STORAGE)
    fun outputStorage() : ContentStorage<Path> {
        return PathBasedStorage(appContext.outputCachePath(), cacheName = ConstNameUUIDBasedCacheName(appContext.outputStorageItemName()))
    }

    @Provides
    @Named(DI_KEY_SOURCES_URL_STORAGE)
    fun sourcesUrlStorage() : ContentStorage<Path> {
        return PathBasedStorage(appContext.sourcesUrlCachePath(), cacheName = ConstNameUUIDBasedCacheName(appContext.sourceUrlItemName()))
    }
}