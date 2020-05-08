package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.client.ktor.di.KtorResult
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.OutputStream

const val FILES_DIR = "files"
const val LONG_OPERATION_FILE_NAME = "diagram_processing.png"
const val ERROR_OPERATION_FILE_NAME = "diagram_error.png"

internal class PredefinedOperationResult(private val fileName: String) :
    KtorResult {
    override fun copyTo(outputStream: OutputStream) {
        FileInputStream("$FILES_DIR/$fileName")
            .use {
                ByteArrayInputStream(it.readBytes()).copyTo(outputStream)
            }
    }
}