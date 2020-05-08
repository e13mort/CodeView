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

import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.classdiagram.ClassDiagram
import net.sourceforge.plantuml.cucadiagram.Member

fun SourceStringReader.asClassDiagram(index: Int): ClassDiagram = blocks[index].diagram as ClassDiagram

fun ClassDiagram.name(entityIndex: Int = 0): String = ArrayList(leafsvalues)[entityIndex].code.fullName

fun ClassDiagram.methods(entityIndex: Int = 0): List<Member> = ArrayList(leafsvalues)[entityIndex].bodier.methodsToDisplay

fun ClassDiagram.fields(entityIndex: Int = 0): List<Member> = ArrayList(leafsvalues)[entityIndex].bodier.fieldsToDisplay

fun Member.type() : String = getDisplay(false).split(" ")[0]

fun Member.name() : String = getDisplay(false).split(" ")[1].split("(")[0]