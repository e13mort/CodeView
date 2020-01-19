package com.github.e13mort.codeview.output.engine

import com.github.e13mort.codeview.StoredObject
import java.io.OutputStream

interface OutputEngine {
    fun saveDataToOutputStream(data: StoredObject, outputStream: OutputStream)
}