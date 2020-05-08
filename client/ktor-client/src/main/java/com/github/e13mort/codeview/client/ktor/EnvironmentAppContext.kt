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

package com.github.e13mort.codeview.client.ktor

import java.nio.file.Path
import java.nio.file.Paths

class EnvironmentAppContext : AppContext {

    override fun logsPath(): Path = Paths.get(readEnv(LOGS_DIR_ENV_KEY))

    override fun gitCachePath(): Path = cachePath().resolve(GIT_CACHE_DIR)

    override fun sourceCachePath(): Path = cachePath().resolve(SOURCE_STORAGE_DIR_NAME)

    override fun backendCachePath(): Path = cachePath().resolve(BACKEND_STORAGE_DIR_NAME)

    override fun backendStorageItemName(): String = BACKEND_STORAGE_ITEM_NAME

    override fun frontendCachePath(): Path = cachePath().resolve(FRONTEND_STORAGE_DIR_NAME)

    override fun frontendStorageItemName(): String = FRONTEND_STORAGE_ITEM_NAME

    override fun outputStorageItemName(): String = OUTPUT_STORAGE_ITEM_NAME

    override fun sourcesUrlCachePath(): Path = cachePath().resolve(SOURCE_URL_STORAGE_DIR_NAME)

    override fun sourceUrlItemName(): String = SOURCE_URL_ITEM_NAME

    override fun outputCachePath(): Path = cachePath().resolve(OUTPUT_STORAGE_DIR_NAME)

    private fun cachePath(): Path = Paths.get(readEnv(CACHE_DIR_ENV_KEY))

    private fun readEnv(name: String): String = System.getenv(name)

    companion object {
        const val LOGS_DIR_ENV_KEY = "logsDir"
        const val CACHE_DIR_ENV_KEY = "cacheDir"

        const val GIT_CACHE_DIR = "gitRepositories"
        const val SOURCE_STORAGE_DIR_NAME = "sources"
        const val BACKEND_STORAGE_DIR_NAME = "backend"
        const val FRONTEND_STORAGE_DIR_NAME = "frontend"
        const val OUTPUT_STORAGE_DIR_NAME = "output"
        const val SOURCE_URL_STORAGE_DIR_NAME = "sourcesUrl"

        const val BACKEND_STORAGE_ITEM_NAME = "classes.json"
        const val FRONTEND_STORAGE_ITEM_NAME = "classes.puml"
        const val OUTPUT_STORAGE_ITEM_NAME = "output.png"
        const val SOURCE_URL_ITEM_NAME = "sources.json"

    }
}