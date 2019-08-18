package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*

class VisitorStoredObject (private val classes: CVClasses) :
    StoredObject {

    override fun asString(): String {
        val builder = StringBuilder()
        builder.append("$START\n")
        classes.accept(CompositeVisitor(builder))
        builder.append("$END\n")
        return builder.toString()
    }

    class CompositeVisitor(private val builder: StringBuilder) :
        CVClasses.Visitor,
        CVClass.FieldsVisitor,
        CVClass.MethodsVisitor,
        CVClass.RelationVisitor {

        override fun onImplementedInterfaceDetected(self: CVClass, implementedInterface: CVClass) {
            builder.append("${implementedInterface.name()} $IMPLEMENTS_ARROW_LEFT ${self.name()}\n")
        }

        override fun onClassDetected(cls: CVClass) {
            builder.append(CLASS)
            process(cls)
        }

        override fun onInterfaceDetected(cls: CVClass) {
            builder.append(INTERFACE)
            process(cls)
        }

        override fun onMethodDetected(
            self: CVClass,
            method: CVMethod
        ) {
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

        override fun onFieldDetected(self: CVClass, field: CVClassField) {
            builder.append("${field.type().simpleName()} ${field.name()}\n")
        }

        private fun process(cls: CVClass) {
            builder.append(" ${cls.name()} {\n")
            cls.accept(this as CVClass.FieldsVisitor)
            cls.accept(this as CVClass.MethodsVisitor)
            builder.append("}\n")
            cls.accept(this as CVClass.RelationVisitor)
        }

    }

}