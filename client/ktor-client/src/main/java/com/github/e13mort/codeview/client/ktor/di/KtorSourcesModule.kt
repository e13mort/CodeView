package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.client.ktor.PredefinedSourcesUrl
import com.github.e13mort.githuburl.SourcesUrl
import dagger.Module
import dagger.Provides

@Module
class KtorSourcesModule {
    @Provides
    fun sources() : SourcesUrl = PredefinedSourcesUrl()
}