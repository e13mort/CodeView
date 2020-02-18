package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.output.EngineBasedOutput
import com.github.e13mort.codeview.output.Target
import com.github.e13mort.codeview.output.engine.PulmOutputEngine
import com.github.e13mort.codeview.output.toCached
import dagger.Module
import dagger.Provides
import java.io.OutputStream
import javax.inject.Named

@Module
class KtorImageOutputModule(private val dataCache: DataCache) {

    @Provides
    fun output(@Named(DI_KEY_OUTPUT_STORAGE) storage: ContentStorage): Output<DataCache.CacheItem> {
        return EngineBasedOutput(
            PulmOutputEngine().toCached(storage),
            CachedResult(dataCache.createCacheItem()))
    }
}

class CachedResult(private val dataCache: DataCache.WritableCacheItem) : Target<DataCache.CacheItem> {
    override fun output(): OutputStream {
        return dataCache.write()
    }

    override fun toResult(): DataCache.CacheItem {
        return dataCache
    }
}
