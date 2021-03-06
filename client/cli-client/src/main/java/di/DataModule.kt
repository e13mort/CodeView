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

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.cache.*
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import com.github.e13mort.codeview.log.ConsoleLog
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import dagger.Module
import dagger.Provides
import factory.LaunchCommand
import java.nio.file.Path
import javax.inject.Named

@Module
class DataModule {
    companion object {
        const val CACHE_FILE_BACK_NAME = "classes.json"
        const val CACHE_FILE_FRONT_NAME = "classes.puml"
        const val CACHE_FOLDER_BACK_NAME = "backend_cache"
        const val CACHE_FOLDER_FRONT_NAME = "frontend_cache"
        const val CACHE_REGISTRY_FILE_NAME = "backend_registry.json"
    }

    @Provides
    fun backend(@Named("backend-storage") contentStorage: KeyValueStorage, log: Log) : Backend {
        return CachedCVTransformation(
            JavaBackend().withLogs(log.withTag("java backend")),
            contentStorage,
            CVActualSerialization()
        ).withLogs(log.withTag("content storage backend"))
    }

    @Provides
    fun frontend(@Named("frontend-storage") contentStorage: KeyValueStorage, log: Log) : Frontend {
        return CachedCVTransformation(
            PulmFrontend().withLogs(log.withTag("pulm frontend")),
            contentStorage,
            StoredObjectActualSerialization()
        ).withLogs(log.withTag("cached frontend"))
    }

    @Named("backend-storage")
    @Provides
    fun contentStorage(log: Log, rootFolder: Path) : KeyValueStorage {
        return PathKeyValueStorage(
            rootFolder.resolve(CACHE_FOLDER_BACK_NAME),
            ConstNameUUIDBasedCacheName(CACHE_FILE_BACK_NAME),
            PathRegistry(rootFolder.resolve(CACHE_FOLDER_BACK_NAME).resolve(CACHE_REGISTRY_FILE_NAME))
        ).withLogs(log.withTag("backed-storage"))
    }

    @Named("frontend-storage")
    @Provides
    fun contentStorageFront(log: Log, rootFolder: Path) : KeyValueStorage {
        return PathKeyValueStorage(
            rootFolder.resolve(CACHE_FOLDER_FRONT_NAME),
            ConstNameUUIDBasedCacheName(CACHE_FILE_FRONT_NAME),
            PathRegistry(rootFolder.resolve(CACHE_FOLDER_FRONT_NAME).resolve(CACHE_REGISTRY_FILE_NAME))
        ).withLogs(log.withTag("frontend-storage"))
    }

    @Provides
    @Named("output-storage")
    fun outputContentStorage(log: Log, rootFolder: Path): KeyValueStorage {
        return PathKeyValueStorage(
            rootFolder.resolve("output-cache"),
            ConstNameUUIDBasedCacheName("output.png"),
            PathRegistry(rootFolder.resolve("output-cache").resolve("registry.json"))
        ).withLogs(log.withTag("output-storage"))
    }

    @Provides
    fun log(launchCommand: LaunchCommand) : Log {
        return if (launchCommand.verbose) ConsoleLog() else Log.EMPTY
    }

    @Provides
    fun pathToStoredObjectTransformation(): CVTransformation<CVTransformation.TransformOperation<Path>, StoredObject> {
        return PathToStoredObjectTransformation()
    }
}