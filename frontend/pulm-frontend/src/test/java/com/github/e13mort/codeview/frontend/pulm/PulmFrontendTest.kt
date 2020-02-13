package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.stubs.*
import io.reactivex.Single
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
            diagram = frontend.prepare(MutableCVClasses.of(StubClass("TestClass")).toTransform()).asDiagram()
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
            diagram = frontend.prepare(
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
            ).asDiagram()
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

            diagram = frontend.prepare(
                classes.toTransform()
            ).asDiagram()
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

            diagram = frontend.prepare(
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
            ).asDiagram()
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
                PulmFrontend().prepare(operation).blockingGet().description(),
                PulmFrontend(setOf(FrontendParams.CLASSES)).prepare(operation).blockingGet().description())
        }

        @Test
        internal fun `same params on the same operation leads to the same descriptions`() {
            val operation = StubBackendTransformOperation()
            assertEquals(
                PulmFrontend().prepare(operation).blockingGet().description(),
                PulmFrontend().prepare(operation).blockingGet().description())
        }

        @Test
        internal fun `same params on different operation leads to different descriptions`() {
            val operation1 = StubBackendTransformOperation("operation1")
            val operation2 = StubBackendTransformOperation("operation2")
            assertNotEquals(
                PulmFrontend().prepare(operation1).blockingGet().description(),
                PulmFrontend().prepare(operation2).blockingGet().description())
        }
    }
}

private fun CVClasses.toTransform() : CVTransformation.TransformOperation<CVClasses> {
    val cvClasses = this
    return object : CVTransformation.TransformOperation<CVClasses> {

        override fun description(): String {
            return "dumb"
        }

        override fun transform(): Single<CVClasses> {
            return Single.just(cvClasses)
        }

    }
}

private fun Single<CVTransformation.TransformOperation<StoredObject>>.asDiagram() : ClassDiagram {
    return flatMap { it.transform() }
        .map { it.asString() }
        .map { SourceStringReader(it) }
        .map { it.asClassDiagram(0) }
        .blockingGet()

}