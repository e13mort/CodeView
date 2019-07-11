package com.github.e13mort.codeview

import io.reactivex.Single

typealias SourceFiles = List<SourceFile>

typealias CVClasses = List<CVClass>

interface StoredObject {
    fun asString(): String
}

interface Backend {
    fun transformSourcesToCVClasses(files: SourceFiles): Single<CVClasses>

}
interface Frontend {
    fun generate(classes: CVClasses): StoredObject
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
