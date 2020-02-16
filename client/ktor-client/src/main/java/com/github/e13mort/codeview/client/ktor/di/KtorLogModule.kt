package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.client.ktor.AppContext
import com.github.e13mort.codeview.log.Log
import com.github.e13mort.codeview.log.impl.SimpleFileLog
import dagger.Module
import dagger.Provides
import java.nio.file.Path
import javax.inject.Named
import javax.inject.Singleton

@Module
class KtorLogModule(private val context: AppContext) {

    companion object {
        const val PATH_KEY = "logFilePath"
        const val LOG_FILE_NAME = "cvktor-logs.txt"
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
        return context.logsPath().resolve(LOG_FILE_NAME)
    }
}