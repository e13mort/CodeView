package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import java.io.OutputStream
import java.io.OutputStreamWriter

class RawOutputEngine : OutputEngine {
    override fun saveDataToOutputStream(
        data: CVTransformation.TransformOperation<StoredObject>,
        outputStream: OutputStream
    ) {
        data
            .transform()
            .map { it.asString() }
            .subscribe { string -> save(outputStream, string) }
    }

    private fun save(outputStream: OutputStream, string: String) {
        OutputStreamWriter(outputStream).use {
            it.write(string)
        }
    }

}