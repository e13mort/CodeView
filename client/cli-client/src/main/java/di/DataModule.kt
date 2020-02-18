package di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import com.github.e13mort.codeview.log.ConsoleLog
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Named

@Module
class DataModule(private val rootFolder: Path) {
    companion object {
        const val CACHE_FILE_BACK_NAME = "classes.json"
        const val CACHE_FILE_FRONT_NAME = "classes.puml"
        const val CACHE_FOLDER_BACK_NAME = "backend_cache"
        const val CACHE_FOLDER_FRONT_NAME = "frontend_cache"
        const val CACHE_REGISTRY_FILE_NAME = "backend_registry.json"
    }

    @Provides
    fun backend(@Named("backend-storage") contentStorage: ContentStorage, log: Log) : Backend {
        return CachedCVTransformation(
            JavaBackend().withLogs(log.withTag("java backend")),
            contentStorage,
            CVActualSerialization(CACHE_FILE_BACK_NAME)
        ).withLogs(log.withTag("content storage backend"))
    }

    @Provides
    fun frontend(@Named("frontend-storage") contentStorage: ContentStorage, log: Log) : Frontend {
        return CachedCVTransformation(
            PulmFrontend().withLogs(log.withTag("pulm frontend")),
            contentStorage,
            StoredObjectActualSerialization(CACHE_FILE_FRONT_NAME)
        ).withLogs(log.withTag("cached frontend"))
    }

    @Named("backend-storage")
    @Provides
    fun contentStorage() : ContentStorage {
        return PathBasedStorage(
            rootFolder.resolve(CACHE_FOLDER_BACK_NAME),
            CACHE_REGISTRY_FILE_NAME,
            ConstNameUUIDBasedCacheName(CACHE_FILE_BACK_NAME)
        )
    }

    @Named("frontend-storage")
    @Provides
    fun contentStorageFront() : ContentStorage {
        return PathBasedStorage(
            rootFolder.resolve(CACHE_FOLDER_FRONT_NAME),
            CACHE_REGISTRY_FILE_NAME,
            ConstNameUUIDBasedCacheName(CACHE_FILE_FRONT_NAME)
        )
    }

    @Provides
    @Named("output-storage")
    fun outputContentStorage(): ContentStorage {
        return PathBasedStorage(rootFolder.resolve("output-cache"), "registry.json", ConstNameUUIDBasedCacheName("output.png"))
    }

    @Provides
    fun log() : Log {
        return ConsoleLog()
    }
}