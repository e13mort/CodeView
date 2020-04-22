package com.github.e13mort.codeview.client.ktor.di

import com.github.e13mort.codeview.output.Target
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class MemoryCacheTarget :
    Target<KtorResult> {

    override fun prepare(): Target.TargetValue<KtorResult> {
        return KtorResulTargetValue()
    }

    internal class KtorResulTargetValue :
        Target.TargetValue<KtorResult> {

        private val data = ByteArrayOutputStream()

        override fun output(): OutputStream {
            return data
        }

        override fun toResult(): KtorResult {
            return object : KtorResult {
                override fun copyTo(outputStream: OutputStream) {
                    ByteArrayInputStream(data.toByteArray()).copyTo(outputStream)
                }

            }
        }
    }
}