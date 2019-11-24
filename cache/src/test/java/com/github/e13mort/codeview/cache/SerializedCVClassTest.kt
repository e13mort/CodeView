package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVClass
import com.github.e13mort.codeview.cache.serialization.SerializableCVClasses
import com.github.e13mort.codeview.cache.serialization.asJson
import com.github.e13mort.codeview.cache.serialization.toSerializedCVClasses
import com.github.e13mort.codeview.describe
import com.github.e13mort.codeview.stubs.StubClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SerializedCVClassTest {
    private val stub: CVClass = StubClass()
    private lateinit var json: String
    private lateinit var serialisedClasses: SerializableCVClasses

    @BeforeEach
    internal fun setUp() {
        serialisedClasses = SerializableCVClasses()
        serialisedClasses.onClassDetected(stub)
        json = serialisedClasses.asJson()
    }

    @Test
    internal fun `serialised string isn't empty`() {
        assertThat(json).isNotBlank()
    }

    @Test
    internal fun `original and de-serialised classes has the same description`() {
        assertEquals(serialisedClasses.describe(), json.toSerializedCVClasses().describe())
    }

    @Test
    internal fun `source class and de-serialised class has the same description`() {
        assertEquals(stub.describe(), json.toSerializedCVClasses().describe())
    }
}