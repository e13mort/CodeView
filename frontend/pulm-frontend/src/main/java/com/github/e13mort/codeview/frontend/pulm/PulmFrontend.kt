package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.StoredObject

class PulmFrontend: Frontend {
    override fun generate(classes: CVClasses): StoredObject {
        return SampleStoredObject(classes)
    }

    private class SampleStoredObject(private val classes: CVClasses) : StoredObject {
        override fun asString(): String {
            val builder = StringBuilder()
            builder.append("@startuml\n")

            classes.forEach {
                builder.append("class ${it.name()} {\n")

                it.fields().forEach {
                    builder.append("${it.type().simpleName()} ${it.name()}\n")
                }

                it.methods().forEach {
                    builder.append("${it.returnType().simpleName()} ${it.name()}(")
                    val parameters = it.parameters()
                    for ((index, it) in parameters.withIndex()) {
                        builder.append("${it.type().simpleName()} ${it.name()}")
                        if (index < parameters.size - 1) {
                            builder.append(", ")
                        }
                    }
                    builder.append(")\n")
                }

                builder.append("}\n")
            }

            builder.append("@enduml\n")
            return builder.toString()
        }

    }
}