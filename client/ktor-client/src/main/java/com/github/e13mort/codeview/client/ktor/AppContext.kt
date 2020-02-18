package com.github.e13mort.codeview.client.ktor

import java.nio.file.Path

interface AppContext {
    fun logsPath(): Path

    fun gitCachePath(): Path

    fun sourceCachePath(): Path

    fun backendCachePath(): Path

    fun frontendCachePath(): Path

    fun backendStorageItemName(): String

    fun frontendStorageItemName(): String
}