package dev.wnuke.mchttpapi.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sun.net.httpserver.HttpServer
import dev.wnuke.mchttpapi.MCHTTPAPI.chatMessages
import dev.wnuke.mchttpapi.utils.ChatMessage
import dev.wnuke.mchttpapi.utils.Login
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer
import dev.wnuke.mchttpapi.utils.ServerConnect
import io.netty.handler.codec.http.HttpResponseStatus
import java.net.InetSocketAddress

class HTTPAPIServer(val compatLayer: MinecraftCompatLayer) {
    private val gson: Gson = GsonBuilder().serializeNulls().create()

    init {
        val server = HttpServer.create(InetSocketAddress(8000), 0)
        JsonGETEndpoint(server, "/chat") { gson.toJson(chatMessages) }
        JsonGETEndpoint(server, "/player") { gson.toJson(compatLayer.playerStats) }
        JsonGETEndpoint(server, "/session") {
            gson.toJson(compatLayer.connection)
        }
        JsonPOSTEndpoint(server, "/sendmsg", needsData = true, block = {
            val (message) = gson.fromJson(it, ChatMessage::class.java)
            if (compatLayer.sendChatMessage(message)) HttpResponseStatus.OK.code() else HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
        })
        JsonPOSTEndpoint(server, "/connect", needsData = true, block = {
            val serverConnect = gson.fromJson(it, ServerConnect::class.java)
            if (compatLayer.connectToServer(serverConnect)) {
                HttpResponseStatus.OK.code()
            } else HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
        })
        JsonPOSTEndpoint(server, "/login", needsData = true, block = {
            val loginData = gson.fromJson(it, Login::class.java)
            if (compatLayer.login(loginData)) {
                HttpResponseStatus.OK.code()
            } else HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
        })
        JsonPOSTEndpoint(server, "/logout", {
            compatLayer.logout()
            HttpResponseStatus.OK.code()
        })
        JsonPOSTEndpoint(server, "/disconnect", {
            compatLayer.disconnect()
            HttpResponseStatus.OK.code()
        })
        server.executor = null
        server.start()
    }
}