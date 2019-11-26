package com.github.e13mort.codeview.log

interface Log {
    fun log(string: String)

    fun log(throwable: Throwable)
}