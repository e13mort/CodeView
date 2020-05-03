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