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

import com.github.e13mort.codeview.CVTransformation
import io.reactivex.Single
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.Exception

internal class CachedTransformOperationTest {

    companion object {
        private const val TEST_DATA = 12
        private val TEST_ERROR = Exception("Test exception")
    }

    @Test
    fun `only one request of original source`() {
        val operationTest = TestTransformOperation(TEST_DATA)
        val cachedOperation = CachedTransformOperation(operationTest)
        operationTest.startCount()
        cachedOperation.transform().test()
        cachedOperation.transform().test()
        cachedOperation.transform().test()
        operationTest.assertCounter(1)
    }

    @Test
    fun `zero requests if transformation was cached`() {
        val operationTest = TestTransformOperation(TEST_DATA)
        val cachedOperation = CachedTransformOperation(operationTest)
        cachedOperation.transform().test()
        operationTest.startCount()
        cachedOperation.transform().test()
        operationTest.assertCounter(0)
    }

    @Test
    fun `provide data from original source`() {
        val operationTest = TestTransformOperation(TEST_DATA)
        val cachedOperation = CachedTransformOperation(operationTest)
        cachedOperation.transform().test().assertValue(TEST_DATA)
    }

    @Test
    fun `provide errors from original source`() {
        val operationTest = ExceptionTransformOperation(TEST_ERROR)
        val cachedOperation = CachedTransformOperation(operationTest)
        cachedOperation.transform().test().assertError(TEST_ERROR)
    }

    internal class ExceptionTransformOperation(private val exception: Exception) : CVTransformation.TransformOperation<Unit> {

        override fun description(): String = "exception operation"

        override fun transform(): Single<Unit> {
            return Single.error(exception)
        }
    }

    internal class TestTransformOperation(private val data: Int) : CVTransformation.TransformOperation<Int> {

        private var subscribeCounter = 0

        private var isCountStarted = false

        override fun description(): String = "test operation"

        override fun transform(): Single<Int> {
            return Single.just(data).doOnSubscribe { if (isCountStarted) subscribeCounter++ }
        }

        fun startCount() {
            isCountStarted = true
        }

        fun assertCounter(expected: Int) {
            Assertions.assertEquals(expected, subscribeCounter)
        }
    }
}