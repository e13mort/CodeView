package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.StoredObject

class PulmFrontend: Frontend {
    override fun generate(classes: CVClasses): StoredObject {
        return object: StoredObject {
            override fun asString(): String {
                return "stub!"
            }

        }
    }
}