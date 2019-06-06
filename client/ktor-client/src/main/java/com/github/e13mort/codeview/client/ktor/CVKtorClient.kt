package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.*
import com.github.e13mort.codeview.frontend.pulm.PulmFrontend
import io.ktor.application.Application
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
        init(this)
    }.start(wait = true)
}

fun Application.main() { init(this) }

fun init(app: Application) {
    app.routing {
        get("/") {
            CodeView(DataSource.EMPTY,
                PulmFrontend(), SampleBackend(), object : Output {
                    override fun save(data: String) {
                        launch {
                            call.respondText(data, ContentType.Text.Plain)
                        }
                    }
                }).run()
        }
    }
}

class SampleBackend : Backend {
    override fun transformSourcesToCVClasses(files: SourceFiles): CVClasses {
        return listOf(
            object : CVClass {
                override fun name(): String = "TestCVClass"

                override fun fields(): List<CVClassField> = emptyList()

                override fun methods(): List<CVMethod> = emptyList()

                override fun has(property: ClassProperty): Boolean = false

            }
        )
    }

}