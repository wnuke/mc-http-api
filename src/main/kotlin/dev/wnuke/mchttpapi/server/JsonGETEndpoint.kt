package dev.wnuke.mchttpapi.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import dev.wnuke.mchttpapi.utils.APIUtils.sendOkJsonResponse
import io.netty.handler.codec.http.HttpResponseStatus

internal abstract class JsonGETEndpoint(private val server: HttpServer, private val path: String) {
    init {
        createContext()
    }

    private fun createContext() {
        server.createContext(path) { he: HttpExchange ->
            if ("GET" == he.requestMethod) {
                sendOkJsonResponse(run(), he)
            } else {
                he.sendResponseHeaders(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), -1L)
            }
            he.close()
        }
    }

    abstract fun run(): String
}