package com.github.e13mort.codeview.work

interface WorkRunner {
    enum class NewWorkState {SCHEDULED, PERFORMED}

    fun schedule(key: String, action: () -> Unit): NewWorkState
}

