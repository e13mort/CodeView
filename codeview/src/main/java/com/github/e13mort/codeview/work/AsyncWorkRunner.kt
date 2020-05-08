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