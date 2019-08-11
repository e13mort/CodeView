package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import java.util.*

class StubClass(private val name: String = "Test",
                private val property: ClassProperty? = null,
                private val methods: List<CVMethod> = Collections.emptyList(),
                private val fields: List<CVClassField> = Collections.emptyList(),
                private val implementedInterfaces: List<CVClass> = Collections.emptyList()) : CVClass {

    override fun implemented(): List<CVClass> = implementedInterfaces

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

class StubField(private val name: String = TODO(),
                private val type: CVType = TODO(),
                private val visibilityModifier: CVVisibility = TODO()) : CVClassField {
    override fun name(): String = name

    override fun type(): CVType = type

    override fun visibilityModificator(): CVVisibility = visibilityModifier

    fun asList(): List<CVClassField> = Collections.singletonList(this)
}

class StubType(private val simpleName: String = TODO(),
               private val fullName: String = TODO()) : CVType {
    override fun simpleName(): String = simpleName

    override fun fullName(): String = fullName
}