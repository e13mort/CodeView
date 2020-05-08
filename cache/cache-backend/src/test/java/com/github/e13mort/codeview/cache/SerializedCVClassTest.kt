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