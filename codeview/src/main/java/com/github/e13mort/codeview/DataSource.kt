package com.github.e13mort.codeview

import io.reactivex.Observable
import io.reactivex.Single
import java.io.InputStream

interface DataSource {
    object EMPTY : DataSource {
        override fun name(): String = "Empty data source"

        override fun sources(path: SourcePath): Single<Sources> = Single.just(Sources.EMPTY)
    }

    fun name(): String

    fun sources(path: SourcePath): Single<Sources>
}

interface Sources {
    object EMPTY : Sources {
        override fun name(): String = "empty sources"

        override fun sources(): Observable<SourceFile> = Observable.empty()
    }

    fun name(): String

    fun sources(): Observable<SourceFile>
}

interface SourceFile {
    fun name(): String

    fun read(): InputStream

    fun fileInfo(): FileInfo

    interface FileInfo {
        fun lastModifiedDate(): Long
    }
}