package korlibs.io.lang

import korlibs.platform.Platform
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JsEvalTest {
    @Test
    fun test() = runTest {
        assertEquals(true, Platform.isJs)
        assertEquals(true, JSEval.available)
        assertEquals(32, JSEval("return a ** b;", "a" to 2, "b" to 5))
        assertEquals(32, JSEval.expr("a ** b", "a" to 2, "b" to 5))
        assertEquals("world2", JSEval.expr("hello + 2", "hello" to "world"))
        //assertEquals(jsGlobal, JSEval.globalThis)
    }

    @Test
    fun testInterface() = runTest {
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
}
