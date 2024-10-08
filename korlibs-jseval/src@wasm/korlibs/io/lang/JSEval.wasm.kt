package korlibs.io.lang

import korlibs.wasm.*
import kotlinx.coroutines.*
import org.khronos.webgl.*
import kotlin.js.Promise

class ExternalJsException(val e: Any?) : Throwable("Error evaluating JS: $e")

actual val JSEval = object : IJSEval {
    override val available: Boolean = true
    override val globalThis: Any? get() = jsGlobal

    override operator fun invoke(
        // language: javascript
        code: String,
        params: Map<String, Any?>,
    ): Any? {
        val keys = params.keys.toList()
        val code = "(function(${keys.joinToString()}) { try { return (function() { $code })(); } catch (e) { return { ___ERROR___ : e }; } })"
        val func = eval(code)
        val global: JsAny = jsGlobal
        val params = keys.map { ensureJsParam(params[it]) }
        //println("CODE: $code")
        //println("PARAMS: $params")
        val args: JsArray<JsAny?> = jsArrayOf(*params.toTypedArray()).unsafeCast()
        //println("ARGS: $args")
        val result = func?.apply(global, args)
        val errorObj = result?.let { jsObjectGet(it, "___ERROR___".toJsString()) }
        if (errorObj != null) {
            throw ExternalJsException(errorObj)
        } else {
            return ensureWasmParam(result)
        }
    }

    override suspend fun invokeSuspend(
        // language: javascript
        code: String,
        params: Map<String, Any?>,
    ): Any? {
        val result = invoke(code, params)
        return ensureWasmParam(when (result) {
            is Promise<*> -> result.await()
            else -> result
        })
    }

    private fun ensureWasmParam(value: Any?): Any? {
        //println("value: value=${value!!::class}")
        return when (value) {
            is Int -> value.toDouble()
            is JsNumber -> value.toDouble()
            is JsString -> value.toString()
            is Int8Array -> value.toByteArray()
            else -> value
        }
    }

    private fun ensureJsParam(value: Any?): JsAny? {
        return when (value) {
            null -> null
            is Long -> value.toJsBigInt()
            is Number -> value.toDouble().toJsNumber()
            is ByteArray -> value.toInt8Array()
            is String -> value.toJsString()
            //is JsAny -> value
            else -> value.toJsReference()
        }
    }
}
