package dev.wnuke.mchttpapi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.multiplayer.ServerData;


public class MinecraftCompatLayer {
    private Minecraft mc;

    public MinecraftCompatLayer() {
        mc = Minecraft.getInstance();
    }

    public APIPlayerStats getPlayerStats() {
        try {
            if (isPlayerNotNull()) {
                APIPlayerStats stats = new APIPlayerStats();
                assert mc.player != null;
                stats.name = mc.player.getName().getString();
                stats.uuid = mc.player.getUniqueID().toString();
                stats.player = new APIPlayerStats.PlayerInfo();
                stats.player.health = mc.player.getHealth();
                stats.player.hunger = mc.player.getFoodStats().getFoodLevel();
                stats.player.saturation = mc.player.getFoodStats().getSaturationLevel();
                stats.coordinates = new APIPlayerStats.Position(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
                return stats;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPlayerNotNull() {
        try {
            if (mc != null) {
                if (mc.player != null) {
                    return true;
                }
            } else {
                mc = Minecraft.getInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendChatMessage(String message) {
        try {
            if (isPlayerNotNull()) {
                assert mc.player != null;
                mc.player.sendChatMessage(message);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disconnectFromServer() {
        try {
            if (mc.world != null) {
                mc.world.sendQuittingDisconnectingPacket();
            }
            if (mc.getConnection() != null) {
                mc.getConnection().getNetworkManager().handleDisconnection();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean connectToServer(RequestTemplates.ServerConnect server) {
        try {
            mc.setServerData(new ServerData("server", server.address, false));
            mc.execute(() -> mc.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), mc, server.address, server.port)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
