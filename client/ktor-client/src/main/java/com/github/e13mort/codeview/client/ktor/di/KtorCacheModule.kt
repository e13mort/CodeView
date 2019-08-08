package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Cache
import com.github.e13mort.codeview.cache.TmpDirBasedCache
import com.github.e13mort.codeview.cache.UUIDCacheName
import dagger.Module
import dagger.Provides

@Module
class KtorCacheModule {
    @Provides
    fun cache() : Cache {
        return TmpDirBasedCache(UUIDCacheName(), "tmp")
    }
}