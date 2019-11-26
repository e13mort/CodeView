package com.github.e13mort.codeview.log

class ConsoleLog : Log {
    override fun log(throwable: Throwable) {
        println(throwable)
    }

    override fun log(string: String) {
        println(string)
    }
}