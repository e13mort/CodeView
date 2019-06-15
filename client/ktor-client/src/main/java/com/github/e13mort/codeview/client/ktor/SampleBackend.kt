package com.github.e13mort.codeview.client.ktor

import com.github.e13mort.codeview.*

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