package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        KtorFrontendModule::class,
        KtorLogModule::class,
        KtorBackendModule::class,
        KtorImageOutputModule::class,
        KtorCacheModule::class
    ]
)
interface KtorComponent {
    fun codeView(): CodeView<DataCache.CacheItem>
}
