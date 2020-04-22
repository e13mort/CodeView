package com.github.e13mort.codeview.output

import java.io.OutputStream

interface Target<T> {
    fun prepare() : TargetValue<T>

    interface TargetValue<T> {

        fun output(): OutputStream

        fun toResult(): T
    }
}