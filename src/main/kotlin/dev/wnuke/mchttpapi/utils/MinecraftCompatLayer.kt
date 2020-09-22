package dev.wnuke.mchttpapi.utils

import com.mojang.authlib.Agent
import com.mojang.authlib.properties.PropertyMap
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import dev.wnuke.mchttpapi.server.Login
import dev.wnuke.mchttpapi.server.PlayerInfo
import dev.wnuke.mchttpapi.server.ServerConnect
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientLoginNetworkHandler
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.NarratorManager
import net.minecraft.client.util.Session
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkState
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Matcher
import java.util.regex.Pattern

class MinecraftCompatLayer(private val minecraft: MinecraftClient) {
    var sessionProperties = PropertyMap()
    var connection: ClientConnection? = null
    var session: Session? = null
    private val startUser = Login(randomUsername())

    private fun randomUsername(): String {
        val chars = "_0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST".toCharArray()
        val randomString = StringBuilder()
        val random: Random = SecureRandom()
        var i = 0
        while (8 > i) {
            val c = chars[random.nextInt(chars.size)]
            randomString.append(c)
            i++
        }
        return randomString.toString()
    }

    fun respawn() {
        minecraft.execute {
            if (null != minecraft.player) {
                if (1.0f > (minecraft.player ?: return@execute).health || minecraft.player!!.isDead) {
                    minecraft.player?.requestRespawn()
                    minecraft.openScreen(null)
                }
            }
        }
    }

    val playerStats: PlayerInfo
        get() {
            val stats = PlayerInfo()
            try {
                if (minecraft.player != null) {
                    assert(null != minecraft.player)
                    stats.name = minecraft.player!!.name.asString()
                    stats.uuid = minecraft.player!!.uuidAsString
                    stats.player.health = minecraft.player!!.health
                    stats.player.hunger = minecraft.player!!.hungerManager.foodLevel.toFloat()
                    stats.player.saturation = minecraft.player!!.hungerManager.saturationLevel
                    stats.coordinates.x = minecraft.player!!.x
                    stats.coordinates.y = minecraft.player!!.y
                    stats.coordinates.z = minecraft.player!!.z
                }
            } catch (_: Exception) {
            }
            return stats
        }

    fun sendChatMessage(message: String): Boolean {
        try {
            println("sending \"$message\"")
            connection?.send(ChatMessageC2SPacket(message))
            return true
        } catch (_: Exception) {
        }
        return false
    }

    fun disconnect() {
        minecraft.networkHandler?.clearWorld()
        minecraft.gameRenderer.reset()
        minecraft.inGameHud.clear()
        minecraft.interactionManager = null
        minecraft.currentServerEntry = null
        minecraft.game.onLeaveGameSession()
        minecraft.world = null
        minecraft.player = null
        NarratorManager.INSTANCE.clear()
    }

    fun connectToServer(server: ServerConnect): Boolean {
        try {
            disconnect()
            minecraft.currentServerEntry = ServerInfo("server", server.address, false)
            val address = server.address
            val port = server.port
            val thread: Thread = object : Thread("Server Connector " + CONNECTOR_THREADS_COUNT.incrementAndGet()) {
                override fun run() {
                    try {
                        println("connecting to $address:$port")
                        val inetAddress = InetAddress.getByName(address)
                        val connectionStart = ClientConnection.connect(inetAddress, port, true)
                        connectionStart.packetListener = ClientLoginNetworkHandler(connection, minecraft, null, null)
                        connectionStart.send(HandshakeC2SPacket(address, port, NetworkState.LOGIN))
                        connectionStart.send(LoginHelloC2SPacket(session!!.profile))
                        connection = connectionStart
                        println("connected")
                    } catch (e: UnknownHostException) {
                        println("$address: unknown host")
                        disconnect()
                    }
                }
            }
            thread.start()
            return true
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
        return false
    }

    fun login(loginData: Login): Boolean {
        try {
            return when {
                loginData.username.isEmpty() -> false
                loginData.password.isEmpty() -> {
                    session = Session(loginData.username, UUID.nameUUIDFromBytes(loginData.username.toByteArray()).toString(), "0", "legacy")
                    true
                }
                else -> {
                    val auth = YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT)
                    auth.setUsername(loginData.username)
                    auth.setPassword(loginData.password)
                    try {
                        auth.logIn()
                    } catch (e: Exception) {
                        println("online login failed, switching to offline mode")
                        val offlineLogin = Login()
                        offlineLogin.username = UNREGEX.matcher(loginData.username).replaceAll(Matcher.quoteReplacement(""))
                        offlineLogin.password = ""
                        login(offlineLogin)
                        return false
                    }
                    session = Session(auth.selectedProfile.name, auth.selectedProfile.id.toString(), auth.authenticatedToken, "mojang")
                    true
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
        return false
    }

    fun logout() {
        disconnect()
        login(startUser)
    }

    override fun toString(): String {
        return "MinecraftCompatLayer{" +
                "sessionProperties=" + sessionProperties +
                ", connection=" + connection +
                ", minecraft=" + minecraft +
                ", session=" + session +
                '}'
    }

    companion object {
        private val CONNECTOR_THREADS_COUNT = AtomicInteger(0)
        private val UNREGEX = Pattern.compile("[^A-Za-z0-9]", Pattern.LITERAL)
    }
}