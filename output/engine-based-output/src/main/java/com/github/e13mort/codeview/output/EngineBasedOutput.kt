package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.output.engine.OutputEngine
import io.reactivex.Single

class EngineBasedOutput<T>(private val engine: OutputEngine, private val target: Target<T>) :
    Output<T> {

    override fun save(data: CVTransformation.TransformOperation<StoredObject>): Single<T> {
        return Single
            .fromCallable { target.output() }
            .flatMapCompletable { engine.saveDataToOutputStream(data, it) }
            .andThen(Single.just(target.toResult()))
    }

}

