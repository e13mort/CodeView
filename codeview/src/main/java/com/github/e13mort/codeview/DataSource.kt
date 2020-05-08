package com.github.e13mort.codeview

import io.reactivex.Single
import java.io.InputStream

interface DataSource {
    object EMPTY : DataSource {
        override fun name(): String = "Empty data source"

        override fun sources(path: SourcePath): Single<Sources> = Single.just(Sources.EMPTY)
    }

    fun name(): String

    fun sources(path: SourcePath): Single<Sources>

    fun describeSources(source: SourcePath): String = "invalid"
}

interface Sources {
    object EMPTY : Sources {
        override fun name(): String = "empty sources"

        override fun sources(): List<SourceFile> = emptyList()
    }

    fun name(): String

    fun sources(): List<SourceFile>
}

interface Content {
    fun read(): InputStream
}

interface SourceFile : Content {
    fun name(): String

    fun fileInfo(): FileInfo

    interface FileInfo {
        companion object EMPTY : FileInfo {
            override fun lastModifiedDate(): Long = 0
        }

        fun lastModifiedDate(): Long
    }
}

fun String.asContent(): Content {
    return object : Content {
        override fun read(): InputStream = this@asContent.toByteArray().inputStream()

    }
}

fun Content.asString(): String {
    return read().reader().readText()
}