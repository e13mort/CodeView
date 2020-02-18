package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.StoredObject
import kotlinx.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class StoredObjectActualSerialization(private val cacheItemName: String) :
    CachedCVTransformation.CVSerialization<StoredObject> {
    override fun serialize(input: StoredObject): Content {
        return object : Content {
            override fun read(): InputStream {
                return ByteArrayInputStream(
                    input.asString().toByteArray(
                        charset = Charset.forName(
                            "UTF-8"
                        )
                    )
                )
            }
        }
    }

    override fun deserialize(path: Path): StoredObject {
        return object : StoredObject {
            override fun asString(): String {
                return Files.readAllBytes(path.resolve(cacheItemName))
                    .toString(charset = Charset.forName("UTF-8"))
            }
        }

    }

}