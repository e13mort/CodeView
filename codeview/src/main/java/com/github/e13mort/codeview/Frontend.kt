package com.github.e13mort.codeview

import io.reactivex.Completable

interface Output {
    fun save(data: String): Completable
}