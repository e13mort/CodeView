package com.github.e13mort.codeview

import io.reactivex.Single

interface Output<T> {
    fun save(data: StoredObject): Single<T>
}