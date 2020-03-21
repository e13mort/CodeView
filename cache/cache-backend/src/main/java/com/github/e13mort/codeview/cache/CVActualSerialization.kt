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