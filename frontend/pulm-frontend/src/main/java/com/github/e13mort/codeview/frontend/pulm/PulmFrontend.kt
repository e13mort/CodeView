package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import io.reactivex.Single

typealias FrontendParams = Set<Frontend.Params>

class PulmFrontend(private val params: FrontendParams = Frontend.Params.all()): Frontend {

    override fun prepareTransformOperation(transformOperation: Backend.TransformOperation): Single<Frontend.TransformOperation> {
        return Single.fromCallable {
            PulmFrontendOperation(transformOperation, params)
        }
    }

    private class PulmFrontendOperation(private val backendOperation: Backend.TransformOperation, private val params: FrontendParams) : Frontend.TransformOperation {
        override fun run(): StoredObject {
            return VisitorStoredObject(backendOperation.run(), params)
        }

        override fun description(): String {
            return FrontendDescription(backendOperation.description(), params).toString()
        }
    }

    private data class FrontendDescription(private val backendDescription: String, private val frontendDescription: Set<Frontend.Params>)
}