package com.github.e13mort.codeview.cache

import java.util.*


class ConstNameUUIDBasedCacheName(private val fileName: String) :
    CacheName {
    override fun createDirName(): String = UUID.randomUUID().toString().replace("-", "")

    override fun createFileName(): String = fileName
}