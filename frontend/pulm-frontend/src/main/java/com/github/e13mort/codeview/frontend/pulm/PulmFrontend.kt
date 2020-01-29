package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import io.reactivex.Single

typealias FrontendParams = Set<Frontend.Params>

class PulmFrontend(private val params: FrontendParams = Frontend.Params.all()): Frontend {

    override fun prepare(source: CVTransformation.TransformOperation<CVClasses>): Single<CVTransformation.TransformOperation<StoredObject>> {
        return Single.fromCallable {
            PulmFrontendOperation(source, params)
        }
    }

    private class PulmFrontendOperation(private val backendOperation: CVTransformation.TransformOperation<CVClasses>, private val params: FrontendParams) : CVTransformation.TransformOperation<StoredObject> {
        override fun run(): StoredObject {
            return VisitorStoredObject(backendOperation.run(), params)
        }

        override fun description(): String {
            return FrontendDescription(backendOperation.description(), params).toString()
        }
    }

    private data class FrontendDescription(private val backendDescription: String, private val frontendDescription: Set<Frontend.Params>)
}