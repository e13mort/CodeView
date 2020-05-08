/*
 * This file is part of CodeView.
 * Copyright (c) 2020 Pavel Novikov
 *
 * CodeView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CodeView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CodeView.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.e13mort.codeview.stubs

import com.github.e13mort.codeview.*
import io.reactivex.Single
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.Exception

class StubClass(private val name: String = "Test",
                private val property: ClassProperty? = null,
                private val methods: List<CVMethod> = listOf(StubMethod()),
                private val fields: List<CVClassField> = listOf(StubField()),
                private val implementedInterfaces: List<CVClass> = emptyList()) :
    CVClass {
    override fun accept(fieldsVisitor: CVClass.FieldsVisitor) {
        fields.forEach {
            fieldsVisitor.onFieldDetected(this, it)
        }
    }

    override fun accept(relationVisitor: CVClass.RelationVisitor) {
        implementedInterfaces.forEach {
            relationVisitor.onImplementedInterfaceDetected(this, it)
        }
    }

    override fun accept(methodsVisitor: CVClass.MethodsVisitor) {
        methods.forEach {
            methodsVisitor.onMethodDetected(this, it)
        }
    }

    override fun has(property: ClassProperty): Boolean = this.property == property

    override fun name(): String = name
}

class StubMethod(private val name: String = "StubMethodName",
                 private val returnType: CVType = StubType(),
                 private val parameters: List<CVMethodParameter> = listOf(StubMethodParameter())) :
    CVMethod {
    override fun name(): String = name

    override fun returnType(): CVType = returnType

    override fun parameters(): List<CVMethodParameter> = parameters

    fun asList() : List<StubMethod> = Collections.singletonList(this)
}

class StubMethodParameter(
    private val name: String = "StubParameter",
    private val type: CVType = StubType()
) : CVMethodParameter {
    override fun name(): String = name

    override fun type(): CVType = type

}

class StubField(private val name: String = "StubFieldName",
                private val type: CVType = StubType(),
                private val visibilityModifier: CVVisibility = CVVisibility.PUBLIC) :
    CVClassField {
    override fun name(): String = name

    override fun type(): CVType = type

    override fun visibilityModificator(): CVVisibility = visibilityModifier

    fun asList(): List<CVClassField> = Collections.singletonList(this)
}

class StubType(private val simpleName: String = "StubSimpleName",
               private val fullName: String = "StubFullName") : CVType {
    override fun simpleName(): String = simpleName

    override fun fullName(): String = fullName
}

class StubCVInput : CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return Single.just(StubCVInputTransformation())
    }
}

class StubCVInputTransformation : CVTransformation.TransformOperation<Path> {
    override fun description(): String = "stub"

    override fun transform(): Single<Path> = Single.just(Paths.get("stub"))
}

class ErrorTransformOperation<T> : CVTransformation.TransformOperation<T> {
    override fun description(): String = "error"

    override fun transform(): Single<T> = Single.error(Exception())
}

class ErrorTransformation<FROM, TO> : CVTransformation<FROM, TO> {
    override fun prepare(source: FROM): Single<CVTransformation.TransformOperation<TO>> {
        return Single.error(Exception())
    }
}

class ErrorCVInput : CVInput {

    override fun prepare(source: SourcePath): Single<CVTransformation.TransformOperation<Path>> {
        return Single.error(Exception("stub"))
    }
}

class StubCVClasses : CVClasses {
    override fun accept(visitor: CVClasses.Visitor) = visitor.onClassDetected(StubClass())
}

class StubFrontendTransformOperation : CVTransformation.TransformOperation<StoredObject> {
    override fun transform(): Single<StoredObject> = Single.just(StubStoreObject())
    override fun description(): String = "stub"
}

class StubBackendTransformOperation(private val description: String = "stub") :
    CVTransformation.TransformOperation<CVClasses> {

    override fun description(): String = description
    override fun transform(): Single<CVClasses> = Single.just(StubCVClasses())
}

class StubStoreObject : StoredObject {
    override fun asString(): String = "stub"
}

class StubOutput<T>(private val instance: T): Output<T> {
    override fun save(data: CVTransformation.TransformOperation<StoredObject>): Single<T> {
        return data.transform().map { instance }
    }
}