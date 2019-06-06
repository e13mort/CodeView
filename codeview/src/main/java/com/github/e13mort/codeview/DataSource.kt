package com.github.e13mort.codeview

import java.io.InputStream
import java.util.*

interface DataSource {
    object EMPTY : DataSource {
        override fun name(): String = "Empty data source"

        override fun sources(): List<SourceFile> = Collections.emptyList()

    }

    fun name(): String

    fun sources(): List<SourceFile>
}

interface SourceFile {
    fun name(): String

    fun read(): InputStream
}