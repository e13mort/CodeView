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
import java.nio.file.FileSystems

@Module
class PredefinedModule {

    companion object {
        const val CACHE_FILE_NAME = "classes.puml"
        const val CACHE_FOLDER_NAME = "backend_cache"
        const val CACHE_REGISTRY_FILE_NAME = "backend_registry.json"
        const val SYSTEM_PROPERTY_HOME = "user.home"
    }

    @Provides
    fun backend(contentStorage: ContentStorage, log: Log) : Backend {
        return ContentStorageBackendCache(
            JavaBackend().withLogs(log.withTag("java backend")),
            contentStorage,
            CVActualSerialization(CACHE_FILE_NAME)
        ).withLogs(log.withTag("content storage backend"))
    }

    @Provides
    fun frontend(log: Log) : Frontend {
        return PulmFrontend().withLogs(log.withTag("pulm frontend"))
    }

    @Provides
    fun contentStorage() : ContentStorage {
        return PathBasedStorage(
            FileSystems.getDefault().getPath(System.getProperty(SYSTEM_PROPERTY_HOME)).resolve(InputModule.CACHE_DIR).resolve(
                CACHE_FOLDER_NAME
            ),
            CACHE_REGISTRY_FILE_NAME,
            ConstNameUUIDBasedCacheName(CACHE_FILE_NAME)
        )
    }

    @Provides
    fun log() : Log {
        return ConsoleLog()
    }
}