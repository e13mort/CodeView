package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*

class VisitorStoredObject(
    private val classes: CVClasses,
    private val params: Set<Frontend.Params>
) :
    StoredObject {

    override fun asString(): String {
        val builder = StringBuilder()
        builder.append("$START\n")
        classes.accept(
            ClassesRenderingVisitor(
                builder,
                MethodsRenderingVisitor(builder),
                FieldsRenderingVisitor(builder),
                ClassRelationsRenderingVisitor(builder),
                params
            )
        )
        builder.append("$END\n")
        return builder.toString()
    }

    class ClassesRenderingVisitor(
        private val builder: StringBuilder,
        private val methodsVisitor: CVClass.MethodsVisitor,
        private val fieldsVisitor: CVClass.FieldsVisitor,
        private val relationsRenderingVisitor: CVClass.RelationVisitor,
        private val params: Set<Frontend.Params>
    ) :
        CVClasses.Visitor {

        override fun onClassDetected(cls: CVClass) {
            //todo: call visitors according to params
            builder.append("${chooseType(cls)} ${cls.name()} {\n")
            cls.accept(fieldsVisitor)
            cls.accept(methodsVisitor)
            builder.append("}\n")
            cls.accept(relationsRenderingVisitor)
        }

        private fun chooseType(cls: CVClass): String {
            return when {
                cls.has(ClassProperty.INTERFACE) -> INTERFACE
                else -> CLASS
            }
        }

    }

    class MethodsRenderingVisitor(private val builder: StringBuilder) : CVClass.MethodsVisitor {

        override fun onMethodDetected(self: CVClass, method: CVMethod) {
            builder.append("${method.returnType().simpleName()} ${method.name()}(")
            val parameters = method.parameters()
            for ((index, it) in parameters.withIndex()) {
                builder.append("${it.type().simpleName()} ${it.name()}")
                if (index < parameters.size - 1) {
                    builder.append(", ")
                }
            }
            builder.append(")\n")
        }

    }

    class FieldsRenderingVisitor(private val builder: StringBuilder) : CVClass.FieldsVisitor {
        override fun onFieldDetected(self: CVClass, field: CVClassField) {
            builder.append("${field.type().simpleName()} ${field.name()}\n")
        }
    }

    class ClassRelationsRenderingVisitor(private val builder: StringBuilder) : CVClass.RelationVisitor {
        override fun onImplementedInterfaceDetected(self: CVClass, implementedInterface: CVClass) {
            builder.append("${implementedInterface.name()} $IMPLEMENTS_ARROW_LEFT ${self.name()}\n")
        }

    }
}