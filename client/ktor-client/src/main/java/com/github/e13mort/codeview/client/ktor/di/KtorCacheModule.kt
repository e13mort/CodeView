package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.Cache
import com.github.e13mort.codeview.CachedCVInput
import com.github.e13mort.codeview.DataSource
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

    @Provides
    fun input(cache: Cache, dataSource: DataSource) : CVInput {
        return CachedCVInput(cache, dataSource)
    }
}