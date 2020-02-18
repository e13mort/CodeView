package com.github.e13mort.codeview.client.ktor

import java.nio.file.Path
import java.nio.file.Paths

class EnvironmentAppContext : AppContext {

    override fun logsPath(): Path = Paths.get(readEnv(LOGS_DIR_ENV_KEY))

    override fun gitCachePath(): Path = cachePath().resolve(GIT_CACHE_DIR)

    override fun sourceCachePath(): Path = cachePath().resolve(SOURCE_STORAGE_DIR_NAME)

    override fun backendCachePath(): Path = cachePath().resolve(BACKEND_STORAGE_DIR_NAME)

    override fun backendStorageItemName(): String = BACKEND_STORAGE_ITEM_NAME

    private fun cachePath(): Path = Paths.get(readEnv(CACHE_DIR_ENV_KEY))

    private fun readEnv(name: String) : String = System.getenv(name)

    companion object {
        const val LOGS_DIR_ENV_KEY = "logsDir"
        const val CACHE_DIR_ENV_KEY = "cacheDir"

        const val GIT_CACHE_DIR = "gitRepositories"
        const val SOURCE_STORAGE_DIR_NAME = "sources"
        const val BACKEND_STORAGE_DIR_NAME = "backend"

        const val BACKEND_STORAGE_ITEM_NAME = "classes.json"
    }
}