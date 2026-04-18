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

    @Test
    fun testUint8Array() {
        if (!JSEval.available) return
        assertEquals(true, JSEval.available, "available")
        val array = JSEval("var array = new Uint8Array(16); array[3] = 7; return array;")
        assertEquals(7.0, JSEval.expr("array[3]", "array" to array))
    }
//* Not working on native
    @Test
    fun testInterface() = runTest {

        if (!JSEval.available) return@runTest
        class TestInterface : IJSEval {
            override val available: Boolean get() = true
            override val globalThis: Any? get() = JSEval.globalThis
            override operator fun invoke(code: String, params: Map<String, Any?>): Any? = JSEval(code, params)
        }

        val testInterface = TestInterface()
        assertEquals(true, testInterface.available)
        assertEquals(10.0, testInterface("return a * b;", "a" to 2, "b" to 5))
        assertEquals(15.0, testInterface.expr("a * b", "a" to 3, "b" to 5))
        assertEquals(20.0, testInterface.exprSuspend("a * b", "a" to 4, "b" to 5))
        assertEquals(25.0, testInterface.invokeSuspend("return a * b;", "a" to 5, "b" to 5))
    }
// */
}
