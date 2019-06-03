package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.CodeView
import com.github.e13mort.codeview.Output
import com.github.e13mort.codeview.backend.java.JavaBackend
import com.github.e13mort.codeview.datasource.filesystem.FileSystemDataSource
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty) {
        routing {
            get("/") {
                CodeView(FileSystemDataSource(
                    "/Users/pavel/work/pets/MyApplication/backend/java-backend"),
                    PulmFrontend(), JavaBackend(), object : Output {
                        override fun save(data: String) {
                            launch {
                                call.respondText(data, ContentType.Text.Plain)
                            }
                        }
                    }).run()
            }
        }
    }.start(wait = true)
}