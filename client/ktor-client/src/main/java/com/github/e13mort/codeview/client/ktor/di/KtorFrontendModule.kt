package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class KtorFrontendModule {

    @Singleton
    @Provides
    fun frontend(): Frontend {
        return PulmFrontend()
    }
}