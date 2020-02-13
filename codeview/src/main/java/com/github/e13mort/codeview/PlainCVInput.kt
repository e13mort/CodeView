package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path
import java.nio.file.Paths

class PlainCVInput : CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return Single.fromCallable {
            object : CVTransformation.TransformOperation<Path> {
                override fun description(): String {
                    return source
                }

                override fun run(): Path {
                    return Paths.get(source)
                }

            }
        }
    }
}