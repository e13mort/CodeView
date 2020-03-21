package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.cache.CachedCVTransformation
import com.github.e13mort.codeview.cache.ContentStorage
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
    fun cachedBackend(@Named(DI_KEY_SOURCE_FRONTEND) source: Frontend, @Named(DI_KEY_FRONTEND_STORAGE) contentStorage: ContentStorage, log: Log) : Frontend {
        return CachedCVTransformation(
            source,
            contentStorage,
            StoredObjectActualSerialization()
        ).withLogs(log.withTag("CachedFrontend"))
    }
}