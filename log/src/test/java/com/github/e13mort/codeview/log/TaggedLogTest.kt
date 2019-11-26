package com.github.e13mort.codeview.log

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test

internal class TaggedLogTest {
    @Test
    internal fun `tag with delimiter added at the beginning of msg`() {
        mock<Log>().let {
            it.withTag("tag").log("msg")
            verify(it).log("tag: msg")
        }
    }

    @Test
    internal fun `throwable is handled`() {
        mock<Log>().let {
            val throwable = Exception()
            it.withTag("tag").log(throwable)
            verify(it).log(eq(throwable))
        }
    }
}