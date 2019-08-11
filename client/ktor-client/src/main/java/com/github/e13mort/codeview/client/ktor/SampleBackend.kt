package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.*
import io.reactivex.Single
import java.nio.file.Path

class SampleBackend : Backend {

    override fun transformSourcesToCVClasses(path: Path): Single<CVClasses> {
        return Single.just(listOf(
            object : CVClass {

                override fun name(): String = "TestCVClass"

                override fun fields(): List<CVClassField> = emptyList()

                override fun methods(): List<CVMethod> = emptyList()

                override fun has(property: ClassProperty): Boolean = false

                override fun implemented(): List<CVClass> = emptyList()

            }
        ))
    }

}