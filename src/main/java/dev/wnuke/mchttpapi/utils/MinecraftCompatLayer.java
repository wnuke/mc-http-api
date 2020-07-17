package dev.wnuke.mchttpapi.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerInfo;


public class MinecraftCompatLayer {
    private final MinecraftClient minecraft;

    public MinecraftCompatLayer(MinecraftClient minecraft) {
        this.minecraft = minecraft;
    }

    public boolean playerNotNull() {
        return this.minecraft.player != null;
    }

    public void respawn() {
        if (playerNotNull()) {
            this.minecraft.execute(() -> {
                assert this.minecraft.player != null;
                this.minecraft.player.requestRespawn();
            });
        }
    }

    public APIPlayerStats getPlayerStats() {
        try {
            if (playerNotNull()) {
                APIPlayerStats stats = new APIPlayerStats();
                assert this.minecraft.player != null;
                stats.name = this.minecraft.player.getName().asString();
                stats.uuid = this.minecraft.player.getUuidAsString();
                stats.player = new APIPlayerStats.PlayerInfo();
                stats.player.health = this.minecraft.player.getHealth();
                stats.player.hunger = this.minecraft.player.getHungerManager().getFoodLevel();
                stats.player.saturation = this.minecraft.player.getHungerManager().getSaturationLevel();
                stats.coordinates = new APIPlayerStats.Position(this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ());
                return stats;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sendChatMessage(String message) {
        try {
            if (playerNotNull()) {
                this.minecraft.execute(() -> {
                    assert this.minecraft.player != null;
                    this.minecraft.player.sendChatMessage(message);
                });
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disconnectFromServer() {
        try {
            this.minecraft.execute(this.minecraft::disconnect);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connectToServer(RequestTemplates.ServerConnect server) {
        try {
            ServerInfo serverInfo = new ServerInfo("server", server.address, false);
            this.minecraft.setCurrentServerEntry(serverInfo);
            this.minecraft.execute(() -> this.minecraft.openScreen(new ConnectScreen(new TitleScreen(), this.minecraft, serverInfo)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
