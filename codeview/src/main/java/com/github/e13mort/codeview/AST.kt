package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path

interface StoredObject {
    fun asString(): String
}

interface Backend : CVTransformation<Path, CVClasses>

interface Frontend : CVTransformation<CVTransformation.TransformOperation<CVClasses>, StoredObject>

interface CVTransformation<FROM, TO> {
    fun prepare(source: FROM): Single<TransformOperation<TO>>

    interface TransformOperation<TO> {
        fun description(): String

        fun run(): TO
    }
}

interface CVClasses {
    fun accept(visitor: Visitor)

    interface Visitor {
        fun onClassDetected(cls: CVClass)
    }
}

interface CVClass {
    fun name(): String

    fun has(property: ClassProperty): Boolean

    fun accept(methodsVisitor: MethodsVisitor)

    fun accept(fieldsVisitor: FieldsVisitor)

    fun accept(relationVisitor: RelationVisitor)

    interface MethodsVisitor {
        fun onMethodDetected(self: CVClass, method: CVMethod)
    }

    interface FieldsVisitor {
        fun onFieldDetected(
            self: CVClass,
            field: CVClassField
        )
    }

    interface RelationVisitor {
        fun onImplementedInterfaceDetected(self: CVClass, implementedInterface: CVClass)
    }
}

interface CVClassField {
    fun name(): String

    fun type(): CVType

    fun visibilityModificator(): CVVisibility
}

interface CVMethod {
    fun name(): String

    fun returnType(): CVType

    fun parameters(): List<CVMethodParameter>
}

interface CVMethodParameter {
    fun name(): String

    fun type(): CVType
}

interface CVType {
    fun simpleName(): String

    fun fullName(): String
}

enum class CVVisibility {
    PUBLIC, PROTECTED, PRIVATE
}

enum class ClassProperty {
    INTERFACE
}

//java|kotlin|cpp -> CodeViewAST -> PULMCode
//java code -> java parser AST (external) -> CodeViewAST -> PULM code
//kotlin code -> kastree AST (external) -> CodeViewAST -> PULM code
