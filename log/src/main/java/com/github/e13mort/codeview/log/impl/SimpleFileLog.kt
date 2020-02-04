package com.github.e13mort.codeview.log.impl

import com.github.e13mort.codeview.log.Log
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class SimpleFileLog(private val filePath: Path) : Log {
    private val params = arrayOf(
        StandardOpenOption.APPEND,
        StandardOpenOption.CREATE
    )

    override fun log(string: String) {
        Files.write(filePath, "$string\n".toByteArray(), *params)
    }

    override fun log(throwable: Throwable) {
        Files.write(filePath, "${throwable.localizedMessage}\n".toByteArray(), *params)
    }
}