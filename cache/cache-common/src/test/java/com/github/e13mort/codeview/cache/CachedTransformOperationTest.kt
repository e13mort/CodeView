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