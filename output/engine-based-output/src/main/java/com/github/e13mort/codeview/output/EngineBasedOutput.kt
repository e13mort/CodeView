package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.output.engine.OutputEngine
import io.reactivex.Single

class EngineBasedOutput<T>(private val engine: OutputEngine, private val target: Target<T>) :
    Output<T> {

    override fun save(data: CVTransformation.TransformOperation<StoredObject>): Single<T> {
        return Single
            .fromCallable { target.prepare() }
            .flatMap { handleDataOperation(data, it) }
    }

    private fun handleDataOperation(
        data: CVTransformation.TransformOperation<StoredObject>,
        it: Target.TargetValue<T>
    ) = engine.saveDataToOutputStream(data, it.output()).toSingleDefault(it.toResult())

}

