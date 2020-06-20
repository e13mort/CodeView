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

    override fun logsPath(): Path = workDirPath().resolve(LOGS_DIR)

    override fun gitCachePath(): Path = workDirPath().resolve(GIT_CACHE_DIR)

    override fun sourceCachePath(): Path = workDirPath().resolve(SOURCE_STORAGE_DIR_NAME)

    override fun backendCachePath(): Path = workDirPath().resolve(BACKEND_STORAGE_DIR_NAME)

    override fun backendStorageItemName(): String = BACKEND_STORAGE_ITEM_NAME

    override fun frontendCachePath(): Path = workDirPath().resolve(FRONTEND_STORAGE_DIR_NAME)

    override fun frontendStorageItemName(): String = FRONTEND_STORAGE_ITEM_NAME

    override fun outputStorageItemName(): String = OUTPUT_STORAGE_ITEM_NAME

    override fun sourcesUrlCachePath(): Path = workDirPath().resolve(SOURCE_URL_STORAGE_DIR_NAME)

    override fun sourceUrlItemName(): String = SOURCE_URL_ITEM_NAME

    override fun outputCachePath(): Path = workDirPath().resolve(OUTPUT_STORAGE_DIR_NAME)

    override fun branchHashItemName(): String = BRANCH_HASH_ITEM_NAME

    override fun branchMetaDirPath(): Path = workDirPath().resolve(BRANCH_META_DIR_NAME)

    override fun branchMetaTTL(): Long = readEnv(BRANCH_META_ENV_KEY).toLongOrNull() ?: BRANCH_META_DEFAULT_TTL_SEC

    override fun string(parameter: AppContext.CVStringParameter): String {
        return readEnvOrFail(parameter.name)
    }

    override fun stringMaybe(parameter: AppContext.CVStringParameter): String? {
        return System.getenv(parameter.name)
    }

    override fun int(parameter: AppContext.CVIntParameter): Int {
        return readInt(parameter.name) ?: throw throw IllegalArgumentException("${parameter.name} isn't found")
    }

    override fun intMaybe(parameter: AppContext.CVIntParameter): Int? {
        return readInt(parameter.name)
    }

    private fun workDirPath(): Path = Paths.get(readEnv(WORK_DIR_ENV_KEY))

    private fun readEnv(name: String): String = System.getenv(name) ?: ""

    private fun readInt(parameter: String) =
        System.getenv(parameter)?.toIntOrNull()

    private fun readEnvOrFail(name: String): String = System.getenv(name) ?: throw IllegalArgumentException("$name isn't found")
}

const val WORK_DIR_ENV_KEY = "workDir"
const val BRANCH_META_ENV_KEY = "branchMetaTTLSec"
const val BRANCH_META_DEFAULT_TTL_SEC = 60L

const val LOGS_DIR = "logs"
const val GIT_CACHE_DIR = "gitRepositories"
const val SOURCE_STORAGE_DIR_NAME = "sources"
const val BACKEND_STORAGE_DIR_NAME = "backend"
const val FRONTEND_STORAGE_DIR_NAME = "frontend"
const val OUTPUT_STORAGE_DIR_NAME = "output"
const val SOURCE_URL_STORAGE_DIR_NAME = "sourcesUrl"
const val BRANCH_META_DIR_NAME = "branchMetaData"

const val BACKEND_STORAGE_ITEM_NAME = "classes.json"
const val FRONTEND_STORAGE_ITEM_NAME = "classes.puml"
const val OUTPUT_STORAGE_ITEM_NAME = "output.png"
const val SOURCE_URL_ITEM_NAME = "sources.json"
const val BRANCH_HASH_ITEM_NAME = "branch_hash.txt"