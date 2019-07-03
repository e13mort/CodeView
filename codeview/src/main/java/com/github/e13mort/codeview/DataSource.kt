package com.github.e13mort.codeview

import io.reactivex.Observable
import java.io.InputStream

interface DataSource {
    object EMPTY : DataSource {
        override fun name(): String = "Empty data source"

        override fun sources(): Observable<SourceFile> = Observable.empty()

    }

    fun name(): String

    fun sources(): Observable<SourceFile>
}

interface SourceFile {
    fun name(): String

    fun read(): InputStream
}