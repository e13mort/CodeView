package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
import io.reactivex.Single

class CachedTransformOperation<OUTPUT>(
    private val decoratedTransformation: CVTransformation.TransformOperation<OUTPUT>
) : CVTransformation.TransformOperation<OUTPUT> by decoratedTransformation {

    private var cachedValue: OUTPUT? = null

    override fun transform(): Single<OUTPUT> {
        return if (cachedValue != null) {
            Single.just(cachedValue)
        } else {
            decoratedTransformation.transform().doOnSuccess { cachedValue = it }
        }
    }

}