package com.github.e13mort.codeview.cache.serialization

import com.github.e13mort.codeview.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
class SerializableCVClasses : CVClasses, CVClasses.Visitor {
    private val classes = mutableListOf<SerializedCVClass>()

    override fun onClassDetected(cls: CVClass) {
        classes += cls.toSerialized()
    }

    override fun accept(visitor: CVClasses.Visitor) {
        classes.forEach {
            visitor.onClassDetected(it)
        }
    }
}

@Serializable
data class SerializedCVClass(
    val name: String,
    val properties: List<ClassProperty>
) : CVClass {

    private val methods = mutableListOf<SerializedCVMethod>()
    private val fields = mutableListOf<SerializedCVCVClassField>()
    private val implementedInterfaces = mutableListOf<SerializedCVClass>()

    override fun name(): String {
        return name
    }

    override fun has(property: ClassProperty): Boolean {
        return properties.contains(property)
    }

    override fun accept(methodsVisitor: CVClass.MethodsVisitor) {
        methods.forEach {
            methodsVisitor.onMethodDetected(this, it)
        }
    }

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

    internal fun methodsVisitor() = MethodsVisitor()

    internal fun fieldsVisitor() = FieldsVisitor()

    internal fun relationsVisitor() = RelationsVisitor()

    inner class MethodsVisitor : CVClass.MethodsVisitor {
        override fun onMethodDetected(self: CVClass, method: CVMethod) {
            methods += method.toSerialized()
        }
    }

    inner class FieldsVisitor : CVClass.FieldsVisitor {
        override fun onFieldDetected(self: CVClass, field: CVClassField) {
            fields += field.toSerialized()
        }
    }

    inner class RelationsVisitor : CVClass.RelationVisitor {
        override fun onImplementedInterfaceDetected(self: CVClass, implementedInterface: CVClass) {
            implementedInterfaces.add(implementedInterface.toSerialized())
        }

    }
}

@Serializable
data class SerializedCVType(val simpleName: String, val fullName: String) : CVType {
    override fun simpleName(): String = simpleName

    override fun fullName(): String = fullName
}

@Serializable
data class SerializedCVMethodParameter(val name: String, val type: SerializedCVType) : CVMethodParameter {
    override fun name(): String = name

    override fun type(): CVType = type
}

@Serializable
data class SerializedCVMethod(
    val name: String,
    val type: SerializedCVType,
    val parameters: List<SerializedCVMethodParameter>
): CVMethod {
    override fun name(): String = name

    override fun returnType(): SerializedCVType = type

    override fun parameters(): List<CVMethodParameter> = parameters

}

@Serializable
data class SerializedCVCVClassField(
    val name: String,
    val type: SerializedCVType,
    val visibilityModifier: CVVisibility
) : CVClassField {
    override fun name(): String = name

    override fun type(): CVType = type

    override fun visibilityModificator(): CVVisibility = visibilityModifier

}

private fun CVMethod.toSerialized() : SerializedCVMethod {
    return SerializedCVMethod(
        name(),
        returnType().toSerialized(),
        parameters().toSerialized()
    )
}

private fun CVClassField.toSerialized() : SerializedCVCVClassField {
    return SerializedCVCVClassField(
        name(),
        type().toSerialized(),
        visibilityModificator()
    )
}

private fun CVType.toSerialized() : SerializedCVType {
    return SerializedCVType(simpleName(), fullName())
}

private fun List<CVMethodParameter>.toSerialized() : List<SerializedCVMethodParameter> {
    val result = mutableListOf<SerializedCVMethodParameter>()
    forEach {
        result += SerializedCVMethodParameter(it.name(), it.type().toSerialized())
    }
    return result
}

private fun CVClass.toSerialized() : SerializedCVClass {
    val source = this
    return SerializedCVClass(name(), emptyList()).apply {
        source.accept(fieldsVisitor())
        source.accept(methodsVisitor())
        source.accept(relationsVisitor())
    }
}

internal fun CVClasses.toSerialized() : SerializableCVClasses {
    val source = this
    return SerializableCVClasses().apply {
        source.accept(this)
    }
}

internal fun SerializableCVClasses.asJson(): String {
    val json = Json(configuration = JsonConfiguration.Stable)
    return json.toJson(SerializableCVClasses.serializer(), this).toString()
}

internal fun String.toSerializedCVClasses(): SerializableCVClasses {
    val json = Json(configuration = JsonConfiguration.Stable)
    return json.parse(SerializableCVClasses.serializer(), this)
}