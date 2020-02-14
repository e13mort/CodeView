package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import com.github.e13mort.codeview.stubs.StubCVBackend
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class KtorBackendModule {

    @Singleton
    @Provides
    fun backend(log: Log): Backend {
        return StubCVBackend().withLogs(log.withTag("StubBackend"))
    }
}