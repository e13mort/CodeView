package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.CVClasses
import com.github.e13mort.codeview.CVVisibility
import com.github.e13mort.codeview.ClassProperty
import com.github.e13mort.codeview.MutableCVClasses
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.classdiagram.ClassDiagram
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class PulmFrontendTest {

    lateinit var frontend: PulmFrontend

    @BeforeEach
    internal fun setUp() {
        frontend = PulmFrontend()
    }

    @Nested
    inner class ClassParsingTest {
        private lateinit var diagram: ClassDiagram

        @BeforeEach
        internal fun setUp() {
            val storedObject = frontend.generate(MutableCVClasses.of(StubClass("TestClass")))
            diagram = SourceStringReader(storedObject.blockingGet().asString()).asClassDiagram(0)
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
            val storedObject = frontend.generate(
                MutableCVClasses.of(
                    StubClass(
                        methods = StubMethod(
                            "sampleMethod",
                            StubType("SampleReturnType", "com.sample.SampleReturnType")
                        ).asList()
                    )
                )
            )
            diagram = SourceStringReader(storedObject.blockingGet().asString()).asClassDiagram(0)
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

    @Nested
    inner class FieldsParsingTest {
        private lateinit var diagram: ClassDiagram

        @BeforeEach
        internal fun setUp() {
            val classes = MutableCVClasses.of(
                StubClass(
                    fields = StubField(
                        "testFieldName",
                        StubType("SampleFieldType", "com.sample.SampleFieldType"),
                        CVVisibility.PUBLIC
                    ).asList()
                )
            )

            val storedObject = frontend.generate(
                classes
            )
            diagram = SourceStringReader(storedObject.blockingGet().asString()).asClassDiagram(0)
        }

        @Test
        internal fun fieldHasAValidName() {
            assertEquals("testFieldName", diagram.fields()[0].name())
        }

        @Test
        internal fun name() {
            assertEquals("SampleFieldType", diagram.fields()[0].type())
        }
    }

    @Nested
    inner class ImplementsParsingTest {
        private lateinit var diagram: ClassDiagram

        @BeforeEach
        internal fun setUp() {
            val storedObject = frontend.generate(
                MutableCVClasses.of(
                    StubClass(
                        name = "TestClass",
                        implementedInterfaces = listOf(
                            StubClass(
                                "TestInterface",
                                property = ClassProperty.INTERFACE
                            )
                        )
                    )
                )
            )

            diagram = SourceStringReader(storedObject.blockingGet().asString()).asClassDiagram(0)
        }

        @Test
        internal fun hasLinkBetweenClassAndInterface() {
            val link = diagram.links[0]
            val firstEntityName = link.entity1.code.fullName
            val secondLinkEntityName = link.entity2.code.fullName
            assertEquals("TestInterface", firstEntityName)
            assertEquals("TestClass", secondLinkEntityName)
        }

        @Test
        internal fun onlyOneLinkOnDiagram() {
            assertEquals(1, diagram.links.size)
        }

    }
}