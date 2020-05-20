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

package com.github.e13mort.codeview.log

import com.github.e13mort.codeview.cache.ContentStorage

class LoggedContentStorage<T>(private val source: ContentStorage<T>, private val log: Log) : ContentStorage<T> by source {

    override fun search(key: String): ContentStorage.ContentStorageItem<T>? {
        log.log("item found for key $key")
        return source.search(key).also {
            log.log("item isn't found for key $key")
        }

    }
}

fun <T>ContentStorage<T>.withLogs(log: Log) : ContentStorage<T> = LoggedContentStorage(this, log)