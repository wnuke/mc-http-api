package dev.wnuke.mchttpapi.server

import com.sun.net.httpserver.HttpServer
import dev.wnuke.mchttpapi.utils.APIUtils.parsePost
import io.netty.handler.codec.http.HttpResponseStatus

class JsonPOSTEndpoint(private val server: HttpServer, private val path: String, val block: (String) -> Int, needsData: Boolean = false) {
    init {
        server.createContext(path) { he ->
            if ("POST" === he.requestMethod) {
                if (needsData) {
                    if (he.requestHeaders.containsKey("Content-Type")) {
                        if ((he.requestHeaders["Content-Type"] ?: "") == "application/json") {
                            he.sendResponseHeaders(block.invoke(parsePost(he)), -1L)
                        }
                        he.sendResponseHeaders(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE.code(), -1L)
                    } else he.sendResponseHeaders(HttpResponseStatus.BAD_REQUEST.code(), -1L)
                } else he.sendResponseHeaders(block.invoke(""), -1L)
            } else {
                he.sendResponseHeaders(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), -1L)
            }
            he.close()
        }
    }
}