package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*

open class StubClass : CVClass {
    override fun fields(): List<CVClassField> = emptyList()

    override fun methods(): List<CVMethod> = emptyList()

    override fun has(property: ClassProperty): Boolean = TODO()

    override fun name(): String = TODO()
}

open class StubMethod : CVMethod {
    override fun name(): String = TODO()

    override fun returnType(): CVType = TODO()

    override fun parameters(): List<CVMethodParameter> = emptyList()
}

open class StubField : CVClassField {
    override fun name(): String = TODO()

    override fun type(): CVType = TODO()

    override fun visibilityModificator(): CVVisibility = TODO()

}

open class StubType : CVType {
    override fun simpleName(): String = TODO()

    override fun fullName(): String = TODO()
}