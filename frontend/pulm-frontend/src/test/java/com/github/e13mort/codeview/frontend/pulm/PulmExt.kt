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