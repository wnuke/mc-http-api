package dev.wnuke.mchttpapi.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerInfo;


public class MinecraftCompatLayer {
    private static MinecraftClient minecraft;

    public MinecraftCompatLayer(MinecraftClient minecraft) {
        MinecraftCompatLayer.minecraft = minecraft;
    }

    public boolean playerNotNull() {
        if (minecraft.player != null) {
            respawn();
            return true;
        }
        return false;
    }

    public void respawn() {
        minecraft.execute(() -> {
            assert minecraft.player != null;
            minecraft.player.requestRespawn();
            minecraft.openScreen(null);
        });
    }

    public APIPlayerStats getPlayerStats() {
        try {
            if (playerNotNull()) {
                APIPlayerStats stats = new APIPlayerStats();
                assert minecraft.player != null;
                stats.name = minecraft.player.getName().asString();
                stats.uuid = minecraft.player.getUuidAsString();
                stats.player = new APIPlayerStats.PlayerInfo();
                stats.player.health = minecraft.player.getHealth();
                stats.player.hunger = minecraft.player.getHungerManager().getFoodLevel();
                stats.player.saturation = minecraft.player.getHungerManager().getSaturationLevel();
                stats.coordinates = new APIPlayerStats.Position(minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ());
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
                minecraft.execute(() -> {
                    assert minecraft.player != null;
                    minecraft.player.sendChatMessage(message);
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
            minecraft.execute(minecraft::disconnect);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connectToServer(RequestTemplates.ServerConnect server) {
        try {
            ServerInfo serverInfo = new ServerInfo("server", server.address, false);
            minecraft.setCurrentServerEntry(serverInfo);
            minecraft.execute(() -> minecraft.openScreen(new ConnectScreen(new TitleScreen(), minecraft, serverInfo)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
