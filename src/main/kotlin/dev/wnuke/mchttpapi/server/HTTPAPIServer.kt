package dev.wnuke.mchttpapi.server

import com.fasterxml.jackson.databind.SerializationFeature
import dev.wnuke.mchttpapi.MCHTTPAPI.chatMessages
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlin.system.exitProcess

class HTTPAPIServer(private val compatLayer: MinecraftCompatLayer) {
    init {
        val ktor = embeddedServer(Netty, 8000) {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }
            routing {
                get("/chat") {
                    call.respond(chatMessages)
                }
                get("/player") {
                    compatLayer.playerStats
                }
                post("/sendmsg") {
                    if (compatLayer.sendChatMessage(call.receive(ChatMessage::class).message)) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.InternalServerError)
                }
                post("/connect") {
                    if (compatLayer.connectToServer(call.receive(ServerConnect::class))) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.InternalServerError)
                }
                post("/login") {
                    if (compatLayer.login(call.receive(Login::class))) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.InternalServerError)
                }
                post("/logout") {
                    compatLayer.logout()
                    call.respond(HttpStatusCode.OK)
                }
                post("/disconnect") {
                    compatLayer.disconnect()
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        try {
            ktor.start()
        } catch (_: Exception) {
            exitProcess(1)
        }
    }
}