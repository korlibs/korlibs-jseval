package korlibs.io.lang

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommonJsEvalTest {
    @Test
    fun test() {
        if (!JSEval.available) return
        assertEquals(true, JSEval.available, "available")
        assertEquals(10.0, JSEval("return a * b;", "a" to 2, "b" to 5))
        assertEquals(10.0, JSEval.expr("a * b", "a" to 2, "b" to 5))
        assertEquals("world2", JSEval.expr("hello + 2", "hello" to "world"))
    }

    @Test
    fun testSuspend() = runTest {
        if (!JSEval.available) return@runTest
        assertEquals(true, JSEval.available, "available")
        assertEquals(10.0, JSEval.invokeSuspend("return a * b;", "a" to 2, "b" to 5))
        assertEquals(10.0, JSEval.exprSuspend("a * b", "a" to 2, "b" to 5))
        assertEquals("world2", JSEval.exprSuspend("hello + 2", "hello" to "world"))
    }
}