/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import com.github.e13mort.codeview.log.*
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
    fun backend(@Named("backend-storage") contentStorage: ContentStorage<Path>, log: Log) : Backend {
        return CachedCVTransformation(
            JavaBackend().withLogs(log.withTag("java backend")),
            contentStorage,
            CVActualSerialization()
        ).withLogs(log.withTag("content storage backend"))
    }

    @Provides
    fun frontend(@Named("frontend-storage") contentStorage: ContentStorage<Path>, log: Log) : Frontend {
        return CachedCVTransformation(
            PulmFrontend().withLogs(log.withTag("pulm frontend")),
            contentStorage,
            StoredObjectActualSerialization()
        ).withLogs(log.withTag("cached frontend"))
    }

    @Named("backend-storage")
    @Provides
    fun contentStorage(log: Log) : ContentStorage<Path> {
        return PathBasedStorage(
            rootFolder.resolve(CACHE_FOLDER_BACK_NAME),
            CACHE_REGISTRY_FILE_NAME,
            ConstNameUUIDBasedCacheName(CACHE_FILE_BACK_NAME)
        ).withLogs(log.withTag("backed-storage"))
    }

    @Named("frontend-storage")
    @Provides
    fun contentStorageFront(log: Log) : ContentStorage<Path> {
        return PathBasedStorage(
            rootFolder.resolve(CACHE_FOLDER_FRONT_NAME),
            CACHE_REGISTRY_FILE_NAME,
            ConstNameUUIDBasedCacheName(CACHE_FILE_FRONT_NAME)
        ).withLogs(log.withTag("frontend-storage"))
    }

    @Provides
    @Named("output-storage")
    fun outputContentStorage(log: Log): ContentStorage<Path> {
        return PathBasedStorage(
            rootFolder.resolve("output-cache"),
            "registry.json",
            ConstNameUUIDBasedCacheName("output.png")
        ).withLogs(log.withTag("output-storage"))
    }

    @Provides
    fun log() : Log {
        return ConsoleLog()
    }
}