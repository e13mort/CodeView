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

package com.github.e13mort.codeview

import io.reactivex.Single
import java.nio.file.Path
import javax.inject.Inject

class PUMLView<T> @Inject constructor(
    private val input: CVTransformation<SourcePath, Path>,
    private val transformation: CVTransformation<CVTransformation.TransformOperation<Path>, StoredObject>,
    private val output: Output<T>
) : CodeView<T> {
    override fun run(parameters: SourcePath): Single<T> {
        return input.prepare(parameters)
            .flatMap(transformation::prepare)
            .flatMap(output::save)
    }
}