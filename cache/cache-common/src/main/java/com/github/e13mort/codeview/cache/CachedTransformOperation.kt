package com.github.e13mort.codeview.cache

import com.github.e13mort.codeview.CVTransformation
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class CachedTransformOperation<OUTPUT>(
    private val decoratedTransformation: CVTransformation.TransformOperation<OUTPUT>
) : CVTransformation.TransformOperation<OUTPUT> by decoratedTransformation {

    private val behaviorSubject: BehaviorSubject<OUTPUT> = BehaviorSubject.create()

    override fun transform(): Single<OUTPUT> {
        return if (behaviorSubject.value != null) {
            Single.just(behaviorSubject.value)
        } else {
            decoratedTransformation.transform().doOnSuccess { behaviorSubject.onNext(it) }
        }
    }

}