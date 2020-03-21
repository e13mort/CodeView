package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.StoredObject
import kotlinx.io.ByteArrayInputStream
import java.io.InputStream

class StoredObjectActualSerialization :
    CachedCVTransformation.CVSerialization<StoredObject> {
    override fun serialize(input: StoredObject): Content {
        return object : Content {
            override fun read(): InputStream {
                return ByteArrayInputStream(
                    input.asString().toByteArray()
                )
            }
        }
    }

    override fun deserialize(content: Content): StoredObject {
        return object : StoredObject {
            override fun asString(): String {
                return content.read().reader().readText()
            }

        }
    }

}