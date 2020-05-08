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

package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import io.reactivex.Single

class PulmFrontend(private val params: Set<FrontendParams> = FrontendParams.all()) : Frontend {

    override fun prepare(source: CVTransformation.TransformOperation<CVClasses>): Single<CVTransformation.TransformOperation<StoredObject>> {
        return Single.fromCallable {
            PulmFrontendOperation(source, params)
        }
    }

    private class PulmFrontendOperation(
        private val backendOperation: CVTransformation.TransformOperation<CVClasses>,
        private val params: Set<FrontendParams>
    ) : CVTransformation.TransformOperation<StoredObject> {

        override fun description(): String {
            return FrontendDescription(backendOperation.description(), params).toString()
        }

        override fun transform(): Single<StoredObject> {
            return backendOperation.transform().map { VisitorStoredObject(it, params) }
        }

        override fun state(): CVTransformation.TransformOperation.OperationState {
            return backendOperation.state()
        }
    }

    private data class FrontendDescription(
        private val backendDescription: String,
        private val frontendDescription: Set<FrontendParams>
    )
}