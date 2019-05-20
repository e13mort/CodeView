package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.StoredObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class PulmFrontend: Frontend {
    override fun save(classes: CVClasses): StoredObject {
        return object: StoredObject {
            override fun save(): OutputStream {
                return ByteArrayOutputStream()
            }

        }
    }
}