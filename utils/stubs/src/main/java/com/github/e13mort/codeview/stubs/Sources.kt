package com.github.e13mort.codeview.stubs

import com.github.e13mort.codeview.*
import io.reactivex.Observable
import io.reactivex.Single
import java.io.InputStream

class StubSourceFile(private val stream: InputStream) : SourceFile {
    override fun name(): String = "stub.file"

    override fun fileInfo(): SourceFile.FileInfo = SourceFile.FileInfo.EMPTY

    override fun read(): InputStream = stream

}

class StubSources(private val source: SourceFile) : Sources {
    override fun name(): String = "stub"

    override fun sources(): Observable<SourceFile> = Observable.just(source)
}

class StubDataSource(private val sources: Sources) : DataSource {
    override fun name(): String {
        return "stub"
    }

    override fun sources(path: SourcePath): Single<Sources> {
        return Single.just(sources)
    }
}

class StubContent(private val stringContent: String = "stub content") : Content {
    override fun read(): InputStream = stringContent.byteInputStream()

}