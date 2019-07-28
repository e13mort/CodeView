package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.CacheRepository
import com.github.e13mort.codeview.backend.java.TmpDirBasedCacheRepository
import com.github.e13mort.codeview.backend.java.UUIDCacheName
import dagger.Module
import dagger.Provides

@Module
class KtorCacheModule {
    @Provides
    fun cache() : CacheRepository {
        return TmpDirBasedCacheRepository(UUIDCacheName())
    }
}