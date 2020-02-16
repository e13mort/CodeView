package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class KtorBackendModule {

    @Singleton
    @Provides
    fun backend(log: Log): Backend {
        return JavaBackend().withLogs(log.withTag("JavaBackend"))
    }
}