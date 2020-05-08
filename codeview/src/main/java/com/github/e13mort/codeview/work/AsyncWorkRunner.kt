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

package com.github.e13mort.codeview.work

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class AsyncWorkRunner : WorkRunner {
    private val executor = Executors.newSingleThreadExecutor()
    private val map: MutableMap<String, Future<*>> = mutableMapOf()
    private val lock = ReentrantLock()

    override fun schedule(key: String, action: () -> Unit): WorkRunner.NewWorkState {
        lock.withLock {
            val existing = map[key]
            if (existing == null || existing.isDone) {
                map[key] = executor.submit(action)
            }
            return WorkRunner.NewWorkState.SCHEDULED
        }
    }
}