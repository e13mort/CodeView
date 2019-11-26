package com.github.e13mort.codeview.log

class TaggedLog(private val source: Log, private val tag: String) : Log {
    override fun log(string: String) {
        source.log("$tag: $string")
    }

    override fun log(throwable: Throwable) {
        source.log(throwable)
    }
}

fun Log.withTag(tag: String) : Log {
    return TaggedLog(this, tag)
}