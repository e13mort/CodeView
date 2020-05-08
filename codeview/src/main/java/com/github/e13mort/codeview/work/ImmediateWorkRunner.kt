package com.github.e13mort.codeview.work

class ImmediateWorkRunner: WorkRunner {
    override fun schedule(key: String, action: () -> Unit): WorkRunner.NewWorkState {
        action()
        return WorkRunner.NewWorkState.PERFORMED
    }
}