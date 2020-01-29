package com.github.e13mort.codeview.output

import com.github.e13mort.codeview.Frontend
import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.output.engine.OutputEngine
import io.reactivex.Single

class EngineBasedOutput<T>(private val engine: OutputEngine, private val target: Target<T>) :
    Output<T> {

    override fun save(data: Frontend.TransformOperation): Single<T> {
        return Single
            .fromCallable { target.output() }
            .doOnSuccess { engine.saveDataToOutputStream(data.run(), it) }
            .map { target.toResult() }
    }

}

