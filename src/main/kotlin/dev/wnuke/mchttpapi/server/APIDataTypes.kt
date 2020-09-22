package dev.wnuke.mchttpapi.server

data class ChatMessage(var message: String = "")

data class Login(var username: String = "", var password: String = "")

data class PlayerInfo(var name: String = "", var uuid: String = "", var player: PlayerStats = PlayerStats(), var coordinates: Position = Position())

data class PlayerStats(var health: Float = 0f, var hunger: Float = 0f, var saturation: Float = 0f)

data class Position(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0)

data class ServerConnect(var address: String = "localhost", var port: Int = 25565)