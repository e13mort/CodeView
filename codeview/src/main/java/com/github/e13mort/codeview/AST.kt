package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path

typealias CVClasses = List<CVClass>

interface StoredObject {
    fun asString(): String
}

interface Backend {
    fun transformSourcesToCVClasses(path: Path) : Single<CVClasses>
}

interface Frontend {
    fun generate(classes: CVClasses): Single<StoredObject>
}

interface CVClass {
    fun name(): String

    fun fields(): List<CVClassField>

    fun methods(): List<CVMethod>

    fun has(property: ClassProperty): Boolean
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

interface CVInterface {
    fun name(): String

    fun methods(): List<CVMethod>
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
