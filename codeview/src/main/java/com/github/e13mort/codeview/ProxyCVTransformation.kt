package com.github.e13mort.codeview

import io.reactivex.Single

class ProxyCVTransformation<FROM> :
    CVTransformation<CVTransformation.TransformOperation<FROM>, FROM> {
    override fun prepare(source: CVTransformation.TransformOperation<FROM>): Single<CVTransformation.TransformOperation<FROM>> {
        return Single.just(transformOperation(source))
    }

    private fun transformOperation(source: CVTransformation.TransformOperation<FROM>): CVTransformation.TransformOperation<FROM> {
        return ProxyTransformOperation(source)
    }

    class ProxyTransformOperation<T>(source: CVTransformation.TransformOperation<T>) :
        CVTransformation.TransformOperation<T> by source

}
