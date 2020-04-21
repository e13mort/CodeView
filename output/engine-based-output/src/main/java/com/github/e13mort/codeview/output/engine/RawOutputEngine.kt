package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import io.reactivex.Completable
import java.io.OutputStream
import java.io.OutputStreamWriter

class RawOutputEngine : OutputEngine {
    override fun saveDataToOutputStream(
        data: CVTransformation.TransformOperation<StoredObject>,
        outputStream: OutputStream
    ): Completable {
        return data
            .transform()
            .flatMapCompletable { save(outputStream, it.asString()) }
    }

    private fun save(outputStream: OutputStream, string: String) : Completable = Completable.fromAction {
        OutputStreamWriter(outputStream).use {
            it.write(string)
        }
    }

}