package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import io.reactivex.Single

class PulmFrontend(private val params: Set<Frontend.Params> = Frontend.Params.all()): Frontend {

    override fun generate(classes: CVClasses): Single<StoredObject> {
        return Single.fromCallable { VisitorStoredObject(classes, params) }
    }
}