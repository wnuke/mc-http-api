package dev.wnuke.mchttpapi.utils

import com.sun.net.httpserver.HttpExchange
import io.netty.handler.codec.http.HttpResponseStatus
import java.io.IOException
import java.nio.charset.StandardCharsets

object APIUtils {
    fun parsePost(he: HttpExchange): String {
        return try {
            val input = he.requestBody
            val sb = StringBuilder()
            var i = input.read()
            while (-1L != i.toLong()) {
                sb.append(i.toChar())
                i = input.read()
            }
            sb.toString()
        } catch (_: IOException) {
            ""
        }
    }

    fun sendOkJsonResponse(message: String, httpExchange: HttpExchange) {
        val messageBytes = message.toByteArray()
        httpExchange.responseHeaders["Content-Type"] = "application/json; charset=" + StandardCharsets.UTF_8
        httpExchange.sendResponseHeaders(HttpResponseStatus.OK.code(), messageBytes.size.toLong())
        val output = httpExchange.responseBody
        output.write(messageBytes)
        output.flush()
    }
}