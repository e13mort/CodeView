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

package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*

class VisitorStoredObject(
    private val classes: CVClasses,
    private val params: Set<FrontendParams>
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
        private val params: Set<FrontendParams>
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