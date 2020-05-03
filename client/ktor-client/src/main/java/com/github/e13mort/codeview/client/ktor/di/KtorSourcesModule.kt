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