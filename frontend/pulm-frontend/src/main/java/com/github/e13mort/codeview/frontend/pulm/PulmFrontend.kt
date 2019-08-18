package com.github.e13mort.codeview.frontend.pulm

import com.github.e13mort.codeview.*
import io.reactivex.Single

class PulmFrontend: Frontend {

    override fun generate(classes: CVClasses): Single<StoredObject> {
        return Single.fromCallable { VisitorStoredObject(classes) }
    }
}