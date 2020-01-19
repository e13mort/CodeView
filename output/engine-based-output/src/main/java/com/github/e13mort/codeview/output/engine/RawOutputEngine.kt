package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.StoredObject
import java.io.OutputStream
import java.io.OutputStreamWriter

class RawOutputEngine : OutputEngine {
    override fun saveDataToOutputStream(data: StoredObject, outputStream: OutputStream) {
        val fileWriter = OutputStreamWriter(outputStream)
        fileWriter.write(data.asString())
        fileWriter.flush()
        fileWriter.close()
    }

}