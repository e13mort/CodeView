package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.Backend
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.impl.SimpleFileLog
import com.github.e13mort.codeview.log.withLogs
import com.github.e13mort.codeview.log.withTag
import com.github.e13mort.codeview.stubs.StubCVBackend
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Named
import javax.inject.Singleton

@Module
class KtorBackendModule {

    companion object {
        const val PATH_KEY = "logFilePath"
        const val WORKING_DIR_ENV_KEY = "logsDir"
        const val LOG_FILE_NAME = "cvktor-logs.txt"
    }

    @Singleton
    @Provides
    fun backend(log: Log): Backend {
        return StubCVBackend().withLogs(log.withTag("StubBackend"))
    }

    @Singleton
    @Provides
    fun log(@Named(PATH_KEY) path: Path) : Log {
        return SimpleFileLog(path)
    }

    @Singleton
    @Provides
    @Named(PATH_KEY)
    fun logFilePath() : Path {
        return Paths.get(System.getenv(WORKING_DIR_ENV_KEY)).resolve(LOG_FILE_NAME)
    }
}