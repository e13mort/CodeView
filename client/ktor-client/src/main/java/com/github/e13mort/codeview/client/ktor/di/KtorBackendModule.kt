package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.client.ktor.SampleBackend
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class KtorBackendModule {

    @Singleton
    @Provides
    fun backend(): Backend {
        return SampleBackend()
    }
}