package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.*
import io.reactivex.Single
import java.nio.file.Path

class SampleBackend : Backend {

    override fun transformSourcesToCVClasses(path: Path): Single<CVClasses> {
        val element = object : CVClass {
            override fun accept(fieldsVisitor: CVClass.FieldsVisitor) = Unit

            override fun accept(methodsVisitor: CVClass.MethodsVisitor) = Unit

            override fun accept(relationVisitor: CVClass.RelationVisitor) = Unit

            override fun name(): String = "TestCVClass"

            override fun has(property: ClassProperty): Boolean = false
        }
        return Single.just(MutableCVClasses.of(element))
    }

}