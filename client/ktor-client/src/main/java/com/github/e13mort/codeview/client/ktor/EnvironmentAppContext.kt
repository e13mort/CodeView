package com.github.e13mort.codeview.client.ktor

import java.nio.file.Path
import java.nio.file.Paths

class EnvironmentAppContext : AppContext {

    override fun logsPath(): Path {
        return Paths.get(readEnv(LOGS_DIR_ENV_KEY))
    }

    override fun gitCachePath(): Path {
        return cachePath().resolve(GIT_CACHE_DIR)
    }

    override fun sourceCachePath(): Path {
        return cachePath().resolve(STORAGE_DIR_NAME)
    }

    private fun cachePath(): Path {
        return Paths.get(readEnv(CACHE_DIR_ENV_KEY))
    }

    private fun readEnv(name: String) : String {
        return System.getenv(name)
    }

    companion object {
        const val LOGS_DIR_ENV_KEY = "logsDir"
        const val CACHE_DIR_ENV_KEY = "cacheDir"

        const val GIT_CACHE_DIR = "gitRepositories"
        const val STORAGE_DIR_NAME = "sources"
    }
}