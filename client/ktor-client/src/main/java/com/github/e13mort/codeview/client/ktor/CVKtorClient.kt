package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty) {
        init(this)
    }.start(wait = true)
}

fun Application.main() { init(this) }

fun init(app: Application) {
    app.routing {
        get("/") {
            CodeView(DataSource.EMPTY, PulmFrontend(), SampleBackend(), ImageOutput(this, call))
                .run()
                .subscribe()
        }
    }
}