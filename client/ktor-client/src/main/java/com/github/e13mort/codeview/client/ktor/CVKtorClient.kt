package com.github.e13mort.codeview.client.ktor

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty) {
        routing {
            get("/") {
                call.respondText("Hello Code View", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}