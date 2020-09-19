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

class HTTPAPIServer(compatLayer: MinecraftCompatLayer) {
    val gson: Gson = GsonBuilder().serializeNulls().create()

    init {
        val server = HttpServer.create(InetSocketAddress(8000), 0)
        object : JsonGETEndpoint(server, "/chat") {
            override fun run(): String {
                return gson.toJson(chatMessages)
            }
        }
        object : JsonGETEndpoint(server, "/player") {
            override fun run(): String {
                return gson.toJson(compatLayer.playerStats)
            }
        }
        object : JsonGETEndpoint(server, "/session") {
            override fun run(): String {
                return gson.toJson(compatLayer.connection)
            }
        }
        object : JsonPOSTEndpoint(server, "/sendmsg", true) {
            override fun run(data: String): Int {
                val (message) = gson.fromJson(data, ChatMessage::class.java)
                return if (compatLayer.sendChatMessage(message)) HttpResponseStatus.OK.code() else HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            }
        }
        object : JsonPOSTEndpoint(server, "/connect", true) {
            override fun run(data: String): Int {
                val serverConnect = gson.fromJson(data, ServerConnect::class.java)
                return if (compatLayer.connectToServer(serverConnect)) {
                    HttpResponseStatus.OK.code()
                } else HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            }
        }
        object : JsonPOSTEndpoint(server, "/login", true) {
            override fun run(data: String): Int {
                val loginData = gson.fromJson(data, Login::class.java)
                return if (compatLayer.login(loginData)) {
                    HttpResponseStatus.OK.code()
                } else HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            }
        }
        object : JsonPOSTEndpoint(server, "/logout") {
            override fun run(data: String): Int {
                compatLayer.logout()
                return HttpResponseStatus.OK.code()
            }
        }
        object : JsonPOSTEndpoint(server, "/disconnect") {
            override fun run(data: String): Int {
                compatLayer.disconnect()
                return HttpResponseStatus.OK.code()
            }
        }
        server.executor = null
        server.start()
    }
}