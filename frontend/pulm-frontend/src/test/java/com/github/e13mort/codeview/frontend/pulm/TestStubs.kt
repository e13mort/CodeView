package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import java.util.*

class StubClass(private val name: String = "Test",
                private val property: ClassProperty? = null,
                private val methods: List<CVMethod> = Collections.emptyList(),
                private val fields: List<CVClassField> = Collections.emptyList()) : CVClass {
    override fun fields(): List<CVClassField> = fields

    override fun methods(): List<CVMethod> = methods

    override fun has(property: ClassProperty): Boolean = this.property == property

    override fun name(): String = name

    fun asList() : List<StubClass> = Collections.singletonList(this)
}

class StubMethod(private val name: String = TODO(),
                 private val returnType: CVType = TODO(),
                 private val parameters: List<CVMethodParameter> = Collections.emptyList()) : CVMethod {
    override fun name(): String = name

    override fun returnType(): CVType = returnType

    override fun parameters(): List<CVMethodParameter> = parameters

    fun asList() : List<StubMethod> = Collections.singletonList(this)
}

open class StubField : CVClassField {
    override fun name(): String = TODO()

    override fun type(): CVType = TODO()

    override fun visibilityModificator(): CVVisibility = TODO()

}

class StubType(private val simpleName: String = TODO(),
               private val fullName: String = TODO()) : CVType {
    override fun simpleName(): String = simpleName

    override fun fullName(): String = fullName
}