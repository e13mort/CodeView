package com.github.e13mort.codeview.frontend.pulm

enum class FrontendParams {
    CLASSES, INTERFACES, RELATIONS;

    companion object {
        fun all(): Set<FrontendParams> {
            return FrontendParams.values().toSet()
        }
    }
}