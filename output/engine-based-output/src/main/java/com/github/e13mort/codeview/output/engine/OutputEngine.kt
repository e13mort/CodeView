package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation.TransformOperation
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Completable
import java.io.OutputStream

interface OutputEngine {
    fun saveDataToOutputStream(data: TransformOperation<StoredObject>, outputStream: OutputStream): Completable
}