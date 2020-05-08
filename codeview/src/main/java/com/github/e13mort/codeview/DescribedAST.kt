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

import java.util.*

fun CVClasses.describe() : Int {
    return mutableListOf<Int>()
        .apply {
            val list = this
            accept(object : CVClasses.Visitor {
                override fun onClassDetected(cls: CVClass) {
                    list += cls.describe()
                }
            })
        }
        .sorted()
        .reduce { acc, i -> combineHashes(acc, i) }
}

fun CVClass.describe() : Int {
    return combineHashes(intArrayOf(describeName(), describeFields(), describeRelations(), describeMethods(), describeProperties()))
}

private fun CVClass.describeMethods() : Int {
    val methods = mutableListOf<CVMethod>()
    this.accept(object : CVClass.MethodsVisitor {
        override fun onMethodDetected(self: CVClass, method: CVMethod) {
            methods += method
        }
    })
    return methods.describe { it.describe() }
}

private fun CVClass.describeFields() : Int {
    val fields = mutableListOf<CVClassField>()
    this.accept(object : CVClass.FieldsVisitor {
        override fun onFieldDetected(self: CVClass, field: CVClassField) {
            fields += field
        }
    })
    return fields.describe { it.describe() }
}

private fun CVClass.describeRelations() : Int {
    val relations = mutableListOf<CVClass>()
    this.accept(object : CVClass.RelationVisitor {
        override fun onImplementedInterfaceDetected(self: CVClass, implementedInterface: CVClass) {
            relations += implementedInterface
        }
    })
    return relations.describe { it.describe() }
}

private fun CVClass.describeProperties() : Int {
    var result = 0
    ClassProperty.values().forEach {
        if (has(it)) result = combineHashes(result, it.ordinal)
    }
    return result
}

private fun CVClassField.describe() : Int {
    return combineHashes(intArrayOf(describeName(), type().describe(), visibilityModificator().ordinal))
}

private fun CVType.describe() : Int {
    return Objects.hash(simpleName(), fullName())
}

fun CVMethod.describe() : Int {
    return combineHashes(intArrayOf(describeName(), returnType().describe(), parameters().describe { it.describe() }))
}

private fun CVMethodParameter.describe() : Int {
    return combineHashes(intArrayOf(name().hashCode(), type().describe()))
}

private fun CVClass.describeName() : Int {
    return name().hashCode()
}

private fun CVMethod.describeName() : Int {
    return name().hashCode()
}

private fun CVClassField.describeName() : Int {
    return name().hashCode()
}

private inline fun <T> Collection<T>.describe(hash: (T) -> Int) : Int {
    val store = TreeSet<Int>()
    forEach {
        store += hash(it)
    }
    return if (store.isNotEmpty()) store.reduce { a, b -> combineHashes(a, b) } else 0
}

private fun combineHashes(source: IntArray) : Int {
    var result = 0
    source.forEach {
        result = combineHashes(result, it)
    }
    return result
}

private fun combineHashes(acc: Int, hash: Int) : Int {
    return 31 * acc + hash
}