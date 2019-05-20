package com.github.e13mort.codeview

import java.io.InputStream

interface DataSource {
    fun name(): String

    fun sources(): List<SourceFile>
}

interface SourceFile {
    fun name(): String

    fun read(): InputStream
}