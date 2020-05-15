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

import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.cache.ContentStorage
import com.github.e13mort.codeview.cache.KeyValueStorage

private class LoggedKeyValueStorage(private val source: KeyValueStorage, private val log: Log) : KeyValueStorage {
    override fun putSingleItem(key: String, content: Content) {
        return source.putSingleItem(key, content).also {
            log.log("put item with key $key")
        }
    }

    override fun searchSingleItem(key: String): ContentStorage.ContentStorageItem? {
        return source.searchSingleItem(key).also {
            log.log("search item for key $key; result = $it")
        }
    }

    override fun remove(key: String) {
        return source.remove(key).also {
            log.log("remove item with key $key")
        }
    }
}

fun KeyValueStorage.withLogs(log: Log): KeyValueStorage = LoggedKeyValueStorage(this, log)