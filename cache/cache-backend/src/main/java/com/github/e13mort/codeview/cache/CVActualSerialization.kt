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

package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.cache.serialization.asJson
import com.github.e13mort.codeview.cache.serialization.toSerialized
import com.github.e13mort.codeview.cache.serialization.toSerializedCVClasses
import java.io.InputStream

class CVActualSerialization :
    CachedCVTransformation.CVSerialization<CVClasses> {

    override fun serialize(input: CVClasses): Content {
        return SerializedCVClassesBasedContent(input)
    }

    override fun deserialize(content: Content): CVClasses {
        return StorageItemBasedCVClasses(content).apply {
            deserialize()
        }
    }

    private class StorageItemBasedCVClasses(
        private val content: Content
    ) : CVClasses {

        private var classes: CVClasses? = null

        override fun accept(visitor: CVClasses.Visitor) {
            classes?.accept(visitor)
        }

        internal fun deserialize() {
            classes = content.read().reader().readText()
                .toSerializedCVClasses()
        }
    }

    private class SerializedCVClassesBasedContent(private val cvClasses: CVClasses) : Content {
        override fun read(): InputStream {
            return cvClasses
                .toSerialized()
                .asJson()
                .byteInputStream()
        }
    }

}