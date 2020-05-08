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

package di

import com.github.e13mort.codeview.output.Target
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileOutputResult(private val fileName: String) :
    Target<String> {

    override fun prepare(): Target.TargetValue<String> {
        return object : Target.TargetValue<String> {
            override fun output(): OutputStream {
                return FileOutputStream(fileName)
            }

            override fun toResult(): String {
                return File(fileName).absolutePath
            }
        }
    }
}