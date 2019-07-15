package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.client.ktor.di.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondOutputStream
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.rx2.await

fun main() {
    embeddedServer(Netty) {
        init(this)
    }.start(wait = true)
}

fun Application.main() { init(this) }

fun init(app: Application) {
    val codeView = DaggerKtorComponent.builder()
        .ktorDataSourceModule(KtorDataSourceModule())
        .ktorBackendModule(KtorBackendModule())
        .ktorFrontendModule(KtorFrontendModule())
        .ktorImageOutputModule(KtorImageOutputModule(MemoryCache()))
        .build().codeView()

    app.routing {
        get("/") {
            call.respondOutputStream(ContentType.Image.PNG, HttpStatusCode.OK) {
                codeView.run().await().copyTo(this)
            }
        }
    }
}
