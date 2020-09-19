package dev.wnuke.mchttpapi.server

import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer
import java.io.IOException
import kotlin.system.exitProcess


class APIServerThread(var minecraftCompatLayer: MinecraftCompatLayer) : Thread() {
    override fun run() {
        try {
            HTTPAPIServer(minecraftCompatLayer)
        } catch (ioException: IOException) {
            println(ioException.localizedMessage)
            exitProcess(1)
        }
    }
}