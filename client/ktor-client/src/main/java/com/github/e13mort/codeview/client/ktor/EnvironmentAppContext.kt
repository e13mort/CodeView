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

        const val BACKEND_STORAGE_ITEM_NAME = "classes.json"
        const val FRONTEND_STORAGE_ITEM_NAME = "classes.puml"
        const val OUTPUT_STORAGE_ITEM_NAME = "output.png"

    }
}