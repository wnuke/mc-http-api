package dev.wnuke.mchttpapi.utils;

import dev.wnuke.mchttpapi.HeadlessAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.multiplayer.ServerData;

public class MinecraftCompatLayer {
    private final Minecraft minecraft;

    public MinecraftCompatLayer(Minecraft minecraft) {
        System.out.println("Loading HeadlessAPI v1.0.0 by wnuke...");
        this.minecraft = minecraft;
        if (this.minecraft != null) {
            HeadlessAPI.startAPIServer(this);
        } else {
            System.out.println("HeadlessAPI failed to load.");
        }
    }

    public boolean playerNotNull() {
        return this.minecraft.player != null;
    }

    public APIPlayerStats getPlayerStats() {
        try {
            if (playerNotNull()) {
                APIPlayerStats stats = new APIPlayerStats();
                assert this.minecraft.player != null;
                stats.name = this.minecraft.player.getName().getString();
                stats.uuid = this.minecraft.player.getUniqueID().toString();
                stats.player = new APIPlayerStats.PlayerInfo();
                stats.player.health = this.minecraft.player.getHealth();
                stats.player.hunger = this.minecraft.player.getFoodStats().getFoodLevel();
                stats.player.saturation = this.minecraft.player.getFoodStats().getSaturationLevel();
                stats.coordinates = new APIPlayerStats.Position(this.minecraft.player.getPosX(), this.minecraft.player.getPosY(), this.minecraft.player.getPosZ());
                return stats;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sendChatMessage(String message) {
        try {
            if (this.minecraft.player != null) {
                this.minecraft.player.sendChatMessage(message);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disconnectFromServer() {
        try {
            if (this.minecraft.world != null) {
                this.minecraft.world.sendQuittingDisconnectingPacket();
            }
            if (this.minecraft.getConnection() != null) {
                this.minecraft.getConnection().getNetworkManager().handleDisconnection();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connectToServer(RequestTemplates.ServerConnect server) {
        try {
            this.minecraft.setServerData(new ServerData("server", server.address, false));
            this.minecraft.execute(() -> this.minecraft.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), this.minecraft, server.address, server.port)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
