package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Single
import java.io.OutputStream

interface OutputEngine {
    fun saveDataToOutputStream(data: CVTransformation.TransformOperation<StoredObject>, outputStream: OutputStream)

    fun saveDataToOutputStreamCompletable(data: CVTransformation.TransformOperation<StoredObject>, outputStream: OutputStream) : Single<OutputStream> {
        return Single.fromCallable {
            saveDataToOutputStream(data, outputStream)
            outputStream
        }
    }
}