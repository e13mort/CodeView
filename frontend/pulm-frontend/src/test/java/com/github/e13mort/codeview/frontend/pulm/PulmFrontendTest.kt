package com.github.e13mort.codeview.frontend.pulm

import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.classdiagram.ClassDiagram
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class PulmFrontendTest {

    @Nested
    inner class ClassParsingTest {
        private lateinit var diagram: ClassDiagram

        @BeforeEach
        internal fun setUp() {
            val storedObject = PulmFrontend().generate(Arrays.asList(StubClass("TestClass")))
            diagram = SourceStringReader(storedObject.asString()).asClassDiagram(0)
        }

        @Test
        internal fun classHasValidName() {
            assertEquals("TestClass", diagram.name())
        }
    }

    @Nested
    inner class MethodParsingTest {

        private lateinit var diagram: ClassDiagram

        @BeforeEach
        internal fun setUp() {
            val storedObject = PulmFrontend().generate(
                StubClass(
                    methods = StubMethod(
                        "sampleMethod",
                        StubType("SampleReturnType", "com.sample.SampleReturnType")
                    ).asList()
                )
                    .asList()
            )
            diagram = SourceStringReader(storedObject.asString()).asClassDiagram(0)
        }

        @Test
        internal fun thereIsOneMethod() {
            assertEquals(1, diagram.methods().size)
        }

        @Test
        internal fun methodHasCorrectName() {
            assertEquals("sampleMethod()", diagram.methods()[0].name())
        }

        @Test
        internal fun methodHasCorrectType() {
            assertEquals("SampleReturnType", diagram.methods()[0].type())
        }

        @Test
        internal fun methodHasUnspecifiedVisibilityModifier() {
            assertNull(diagram.methods()[0].visibilityModifier)
        }
    }
}