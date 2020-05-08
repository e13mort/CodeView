/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

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