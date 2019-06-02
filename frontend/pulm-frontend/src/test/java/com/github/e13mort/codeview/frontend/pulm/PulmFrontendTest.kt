package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.CVMethod
import com.github.e13mort.codeview.CVType
import com.github.e13mort.codeview.ClassProperty
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.classdiagram.ClassDiagram
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList

class PulmFrontendTest {

    @Nested
    inner class MethodParsingTest {

        private lateinit var diagram: ClassDiagram

        @BeforeEach
        internal fun setUp() {
            val storedObject = PulmFrontend().generate(Arrays.asList(
                object : StubClass() {

                    override fun methods(): List<CVMethod> = Arrays.asList(object : StubMethod() {
                        override fun name(): String = "sampleMethod"

                        override fun returnType(): CVType = object : StubType() {
                            override fun simpleName(): String = "SampleReturnType"
                        }
                    })

                    override fun name(): String = "TestClass"

                    override fun has(property: ClassProperty): Boolean {
                        return property != ClassProperty.INTERFACE
                    }
                }
            ))
            diagram = SourceStringReader(storedObject.asString()).blocks[0].diagram as ClassDiagram
        }

        @Test
        internal fun thereIsOneMethod() {
            assertEquals(1, methods().size)
        }

        @Test
        internal fun methodHasCorrectName() {
            assertEquals("sampleMethod()", methodName())
        }

        @Test
        internal fun methodHasCorrectType() {
            assertEquals("SampleReturnType", methodType())
        }

        @Test
        internal fun methodHasUnspecifiedVisibilityModifier() {
            assertNull(methods()[0].visibilityModifier)
        }

        @Test
        internal fun classHasValidName() {
            assertEquals("TestClass", className())
        }

        private fun methods() = ArrayList(diagram.leafsvalues)[0].bodier.methodsToDisplay

        private fun methodType() = methods()[0].getDisplay(false).split(" ")[0]

        private fun methodName() = methods()[0].getDisplay(false).split(" ")[1]

        private fun className() = ArrayList(diagram.leafsvalues)[0].code.fullName
    }
}