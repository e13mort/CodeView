package di

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.cache.CacheName
import com.github.e13mort.codeview.cache.TmpDirBasedCache
import com.github.e13mort.codeview.cache.UUIDCacheName
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import dagger.Module
import dagger.Provides

@Module
class PredefinedModule {

    @Provides
    fun backend() : Backend {
        return JavaBackend()
    }

    @Provides
    fun frontend() : Frontend {
        return PulmFrontend()
    }

    @Provides
    fun cache(cacheName: CacheName) : Cache {
        return TmpDirBasedCache(cacheName, createDirName(cacheName))
    }

    @Provides
    fun cacheName(): CacheName {
        return UUIDCacheName()
    }

    @Provides
    fun input(cache: Cache, dataSource: DataSource) : CVInput {
        return CachedCVInput(cache, dataSource)
    }

    private fun createDirName(cacheName: CacheName) = "tmp${cacheName.createDirName()}"
}