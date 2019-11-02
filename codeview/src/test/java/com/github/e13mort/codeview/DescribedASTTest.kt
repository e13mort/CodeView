package com.github.e13mort.codeview

import com.github.e13mort.codeview.stubs.StubClass
import com.github.e13mort.codeview.stubs.StubField
import com.github.e13mort.codeview.stubs.StubMethod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DescribedASTTest {
    //TODO add more tests

    @Test
    internal fun `Describe method doesn't return 0`() {
        assertNotEquals(0, StubClass().describe())
    }

    @Test
    internal fun `two equal class has the same description`() {
        assertEquals(StubClass().describe(), StubClass().describe())
    }

    @Test
    internal fun `two not equal classes has different descriptions`() {
        assertNotEquals(StubClass("A").describe(), StubClass("B").describe())
    }

    @Test
    internal fun `single instance changes description after modifications`() {
        val methods = mutableListOf<CVMethod>()
        val stubClass = StubClass("Class", methods = methods)
        val originalDescription = stubClass.describe()
        methods += StubMethod()
        assertNotEquals(originalDescription, stubClass.describe())
    }

    @Test
    internal fun `cv classes with different class order has the same description`() {
        val classA = StubClass("A")
        val classB = StubClass("B")
        assertEquals(
            MutableCVClasses().apply {
                add(classA)
                add(classB)
            }.describe(),
            MutableCVClasses().apply {
                add(classB)
                add(classA)
            }.describe()
        )
    }

    @Nested
    inner class MethodsTest {

        @Test
        internal fun `methods order doesn't affect on description`() {
            val methodA = StubMethod("A")
            val methodB = StubMethod("B")
            val directOrderMethods = StubClass("Class", methods = listOf<CVMethod>(methodA, methodB))
            val reverseOrderMethods = StubClass("Class", methods = listOf<CVMethod>(methodB, methodA))
            assertEquals(directOrderMethods.describe(), reverseOrderMethods.describe())
        }

        @Test
        internal fun `equal name`() {
            assertEquals(StubMethod("A").describe(), StubMethod("A").describe())
        }

        @Test
        internal fun `different names`() {
            assertNotEquals(StubMethod("A").describe(), StubMethod("B").describe())
        }

    }

    @Nested
    inner class FieldsTest {

        @Test
        internal fun `fields order doesn't affect on description`() {
            val fieldA = StubField("A")
            val fieldB = StubField("B")
            val directOrderMethods = StubClass("Class", fields = listOf<CVClassField>(fieldA, fieldB))
            val reverseOrderMethods = StubClass("Class", fields = listOf<CVClassField>(fieldB, fieldA))
            assertEquals(directOrderMethods.describe(), reverseOrderMethods.describe())
        }

    }

    @Nested
    inner class RelationsTest {
        @Test
        internal fun relations() {
            val iclass = StubClass("Interface", property = ClassProperty.INTERFACE)
            assertEquals(
                StubClass(implementedInterfaces = listOf(iclass)).describe(),
                StubClass(implementedInterfaces = listOf(iclass)).describe()
            )
        }
    }
}