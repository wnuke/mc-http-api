package dev.wnuke.mchttpapi.utils;

import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.multiplayer.ServerData;

import static dev.wnuke.mchttpapi.HeadlessAPI.minecraft;


public class MinecraftCompatLayer {
    public APIPlayerStats getPlayerStats() {
        try {
            if (isPlayerNotNull()) {
                APIPlayerStats stats = new APIPlayerStats();
                assert minecraft.player != null;
                stats.name = minecraft.player.getName().getString();
                stats.uuid = minecraft.player.getUniqueID().toString();
                stats.player = new APIPlayerStats.PlayerInfo();
                stats.player.health = minecraft.player.getHealth();
                stats.player.hunger = minecraft.player.getFoodStats().getFoodLevel();
                stats.player.saturation = minecraft.player.getFoodStats().getSaturationLevel();
                stats.coordinates = new APIPlayerStats.Position(minecraft.player.getPosX(), minecraft.player.getPosY(), minecraft.player.getPosZ());
                return stats;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPlayerNotNull() {
        try {
            if (minecraft.player != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendChatMessage(String message) {
        try {
            if (isPlayerNotNull()) {
                assert minecraft.player != null;
                minecraft.player.sendChatMessage(message);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disconnectFromServer() {
        try {
            if (minecraft.world != null) {
                minecraft.world.sendQuittingDisconnectingPacket();
            }
            if (minecraft.getConnection() != null) {
                minecraft.getConnection().getNetworkManager().handleDisconnection();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connectToServer(RequestTemplates.ServerConnect server) {
        try {
            minecraft.setServerData(new ServerData("server", server.address, false));
            minecraft.execute(() -> minecraft.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), minecraft, server.address, server.port)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
