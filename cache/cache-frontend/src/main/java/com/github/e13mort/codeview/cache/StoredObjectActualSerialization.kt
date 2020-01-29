package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.Content
import com.github.e13mort.codeview.StoredObject
import kotlinx.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class StoredObjectActualSerialization :
    CachedCVTransformation.CVSerialization<StoredObject> {
    override fun content(storedObject: StoredObject): Content {
        return object : Content {
            override fun read(): InputStream {
                return ByteArrayInputStream(
                    storedObject.asString().toByteArray(
                        charset = Charset.forName(
                            "UTF-8"
                        )
                    )
                )
            }
        }
    }

    override fun classes(path: Path): StoredObject {
        return object : StoredObject {
            override fun asString(): String {
                return Files.readAllBytes(path)
                    .toString(charset = Charset.forName("UTF-8"))
            }
        }

    }

}