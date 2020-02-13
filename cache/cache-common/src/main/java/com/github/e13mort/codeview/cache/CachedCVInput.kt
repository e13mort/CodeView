package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVInput
import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.DataSource
import com.github.e13mort.codeview.SourcePath
import io.reactivex.Single
import java.nio.file.Path

class CachedCVInput(private val cache: Cache, private val dataSource: DataSource) :
    CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return Single.fromCallable {
            object : CVTransformation.TransformOperation<Path> {
                override fun description(): String {
                    return source
                }

                override fun transform(): Single<Path> {
                    return dataSource.sources(source)
                        .flatMap { cache.cacheSources(it) }
                        .map { it.files() }
                }

            }
        }
    }
}