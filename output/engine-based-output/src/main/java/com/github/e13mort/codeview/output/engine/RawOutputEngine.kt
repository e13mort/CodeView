package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.CVTransformation
import com.github.e13mort.codeview.StoredObject
import java.io.OutputStream
import java.io.OutputStreamWriter

class RawOutputEngine : OutputEngine {
    override fun saveDataToOutputStream(data: CVTransformation.TransformOperation<StoredObject>, outputStream: OutputStream) {
        val fileWriter = OutputStreamWriter(outputStream)
        fileWriter.write(data.run().asString())
        fileWriter.flush()
        fileWriter.close()
    }

}