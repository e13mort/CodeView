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

package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path

interface StoredObject {
    fun asString(): String
}

typealias CVInput = CVTransformation<SourcePath, Path>

typealias Backend = CVTransformation<CVTransformation.TransformOperation<Path>, CVClasses>

typealias Frontend = CVTransformation<CVTransformation.TransformOperation<CVClasses>, StoredObject>

interface CVTransformation<FROM, TO> {
    fun prepare(source: FROM): Single<TransformOperation<TO>>

    interface TransformOperation<TO> {
        fun description(): String

        /**
         * Should display a possible future error if operation couldn't be executed
         * */
        fun state(): OperationState = OperationState.READY //

        /**
         * Might emit an error if operation is failed
         * */
        fun transform(): Single<TO> //

        enum class OperationState {
            READY, ERROR
        }

        class LongOperationException: Exception()
    }
}

interface CVClasses {
    fun accept(visitor: Visitor)

    interface Visitor {
        fun onClassDetected(cls: CVClass)
    }
}

interface CVClass {
    fun name(): String

    fun has(property: ClassProperty): Boolean

    fun accept(methodsVisitor: MethodsVisitor)

    fun accept(fieldsVisitor: FieldsVisitor)

    fun accept(relationVisitor: RelationVisitor)

    interface MethodsVisitor {
        fun onMethodDetected(self: CVClass, method: CVMethod)
    }

    interface FieldsVisitor {
        fun onFieldDetected(
            self: CVClass,
            field: CVClassField
        )
    }

    interface RelationVisitor {
        fun onImplementedInterfaceDetected(self: CVClass, implementedInterface: CVClass)
    }
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
