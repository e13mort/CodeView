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

package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content

private class MemoryBasedLimitedTimeContentStorage(
    private val source: KeyValueStorage,
    private val time: Time,
    private val ttlSeconds: Long
) : KeyValueStorage {

    private val timeMap = mutableMapOf<String, Long>()

    override fun put(key: String, content: Content) {
        return source.put(key, content).also {
            timeMap[key] = time.nowMillis()
        }
    }

    override fun search(key: String): Content? {
        val putTime = timeMap[key] ?: return null
        return if (putTime + ttlSeconds * 1000 > time.nowMillis()) source.search(key) else null
    }

    override fun remove(key: String) {
        throw UnsupportedOperationException()
    }

    override fun data(): Map<String, Content> {
        return source.data()
    }
}

fun KeyValueStorage.withTimeLimit(ttlSeconds: Long, time: Time = Time.REAL): KeyValueStorage {
    return MemoryBasedLimitedTimeContentStorage(this, time, ttlSeconds)
}