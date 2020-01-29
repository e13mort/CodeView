package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.stubs.*
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.classdiagram.ClassDiagram
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
            val storedObject = frontend.prepareTransformOperation(MutableCVClasses.of(StubClass("TestClass")).toTransform())
            diagram = SourceStringReader(storedObject.blockingGet().run().asString()).asClassDiagram(0)
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
            val storedObject = frontend.prepareTransformOperation(
                MutableCVClasses.of(
                    StubClass(
                        methods = StubMethod(
                            "sampleMethod",
                            StubType(
                                "SampleReturnType",
                                "com.sample.SampleReturnType"
                            )
                        ).asList()
                    )
                ).toTransform()
            )
            diagram = SourceStringReader(storedObject.blockingGet().run().asString()).asClassDiagram(0)
        }

        @Test
        internal fun thereIsOneMethod() {
            assertEquals(1, diagram.methods().size)
        }

        @Test
        internal fun methodHasCorrectName() {
            assertEquals("sampleMethod", diagram.methods()[0].name())
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

            val storedObject = frontend.prepareTransformOperation(
                classes.toTransform()
            )
            diagram = SourceStringReader(storedObject.blockingGet().run().asString()).asClassDiagram(0)
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
            val storedObject = frontend.prepareTransformOperation(
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
                ).toTransform()
            )

            diagram = SourceStringReader(storedObject.blockingGet().run().asString()).asClassDiagram(0)
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

    @Nested
    inner class TransformDescriptionTest {

        @Test
        internal fun `different params on the same operation leads to different descriptions`() {
            val operation = StubBackendTransformOperation()
            assertNotEquals(
                PulmFrontend().prepareTransformOperation(operation).blockingGet().description(),
                PulmFrontend(setOf(Frontend.Params.CLASSES)).prepareTransformOperation(operation).blockingGet().description())
        }

        @Test
        internal fun `same params on the same operation leads to the same descriptions`() {
            val operation = StubBackendTransformOperation()
            assertEquals(
                PulmFrontend().prepareTransformOperation(operation).blockingGet().description(),
                PulmFrontend().prepareTransformOperation(operation).blockingGet().description())
        }

        @Test
        internal fun `same params on different operation leads to different descriptions`() {
            val operation1 = StubBackendTransformOperation("operation1")
            val operation2 = StubBackendTransformOperation("operation2")
            assertNotEquals(
                PulmFrontend().prepareTransformOperation(operation1).blockingGet().description(),
                PulmFrontend().prepareTransformOperation(operation2).blockingGet().description())
        }
    }
}

private fun CVClasses.toTransform() : Backend.TransformOperation {
    val cvClasses = this
    return object : Backend.TransformOperation {
        override fun run(): CVClasses {
            return cvClasses
        }

        override fun description(): String {
            return "dumb"
        }

    }
}