package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.StoredObject
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import net.sourceforge.plantuml.SourceStringReader

@Module
class KtorImageOutputModule(private val dataCache: DataCache) {

    @Provides
    fun output(): Output<DataCache.CacheItem> {
        return CachedOutput(dataCache)
    }
}

class CachedOutput(private val dataCache: DataCache) : Output<DataCache.CacheItem> {
    override fun save(data: StoredObject): Single<DataCache.CacheItem> {
        return Single.just(dataCache.createCacheItem())
            .doOnEvent { item, _ ->
                SourceStringReader(data.asString()).outputImage(item.write())
            }.map {
                it as DataCache.CacheItem
            }

    }
}