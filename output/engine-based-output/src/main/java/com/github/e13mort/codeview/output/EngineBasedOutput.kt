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

