package dev.wnuke.mchttpapi

import dev.wnuke.mchttpapi.server.HTTPAPIServer
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import kotlin.system.exitProcess

object MCHTTPAPI : ModInitializer {
    val chatMessages = ArrayList<String>()
    var compatLayer: MinecraftCompatLayer? = null

    override fun onInitialize() {
        println("Loading MC HTTP API v1.0.0 by wnuke...")
        if (MinecraftClient.getInstance() == null) {
            println("Minecraft instance is null, exiting.")
            exitProcess(1)
        }
        compatLayer = MinecraftCompatLayer(MinecraftClient.getInstance())
        compatLayer?.let { HTTPAPIServer(it) }
        println("MC HTTP API loaded!")
    }
}