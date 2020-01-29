package com.github.e13mort.codeview.stubs

import com.github.e13mort.codeview.*
import io.reactivex.Single
import java.lang.Exception
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

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
    override fun handleInput(path: SourcePath): Single<Path> {
        return Single.just(Paths.get("stub"))
    }
}

class ErrorCVInput : CVInput {
    override fun handleInput(path: SourcePath): Single<Path> {
        return Single.error(Exception("stub"))
    }
}

class StubCVBackend : Backend {
    override fun prepareTransformOperation(path: Path): Single<Backend.TransformOperation> = Single.just(StubBackendTransformOperation())
}

class ErrorCVBackend : Backend {
    override fun prepareTransformOperation(path: Path): Single<Backend.TransformOperation> = Single.error(Exception())
}

class StubCVClasses : CVClasses {
    override fun accept(visitor: CVClasses.Visitor) = Unit
}

class StubCVFrontend : Frontend {
    override fun prepareTransformOperation(transformOperation: Backend.TransformOperation): Single<Frontend.TransformOperation> = Single.just(StubFrontendTransformOperation())
}

class ErrorCVFrontend : Frontend {
    override fun prepareTransformOperation(transformOperation: Backend.TransformOperation): Single<Frontend.TransformOperation> = Single.error(Exception())
}

class StubFrontendTransformOperation : Frontend.TransformOperation {
    override fun run(): StoredObject = StubStoreObject()

    override fun description(): String = "stub"
}

class StubBackendTransformOperation(private val description: String = "stub") : Backend.TransformOperation {
    override fun run(): CVClasses = StubCVClasses()

    override fun description(): String = description
}

class StubStoreObject : StoredObject {
    override fun asString(): String = "stub"
}

class StubOutput<T>(private val instance: T): Output<T> {
    override fun save(data: Frontend.TransformOperation): Single<T> {
        return Single.just(instance)
    }
}

class ErrorOutput<T>(): Output<T> {
    override fun save(data: Frontend.TransformOperation): Single<T> {
        return Single.error(Exception("stub"))
    }
}