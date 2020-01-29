package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.cache.serialization.asJson
import com.github.e13mort.codeview.cache.serialization.toSerialized
import com.github.e13mort.codeview.cache.serialization.toSerializedCVClasses
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class CVActualSerialization(private val fileName: String) :
    CachedCVTransformation.CVSerialization<CVClasses> {

    override fun content(classes: CVClasses): Content {
        return SerializedCVClassesBasedContent(classes)
    }

    override fun classes(path: Path): CVClasses {
        return StorageItemBasedCVClasses(path.resolve(fileName)).apply {
            deserialize()
        }
    }

    private class StorageItemBasedCVClasses(
        private val path: Path
    ) : CVClasses {

        private var classes: CVClasses? = null

        override fun accept(visitor: CVClasses.Visitor) {
            classes?.accept(visitor)
        }

        internal fun deserialize() {
            classes = Files.readAllLines(path)
                .reduce { acc, s -> acc.plus(s) }
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