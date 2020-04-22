package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.datasource.git.di.GitDataSourceModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        KtorFrontendModule::class,
        KtorLogModule::class,
        KtorBackendModule::class,
        KtorImageOutputModule::class,
        KtorCacheModule::class,
        KtorSourcesModule::class,
        GitDataSourceModule::class
    ]
)
interface KtorComponent {
    fun codeView(): CodeView<KtorResult>
}
