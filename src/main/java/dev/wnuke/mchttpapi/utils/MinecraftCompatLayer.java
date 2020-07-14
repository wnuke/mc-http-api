package dev.wnuke.mchttpapi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.multiplayer.ServerData;

import static dev.wnuke.mchttpapi.HeadlessAPI.status;


public class MinecraftCompatLayer {
    private static final Minecraft mc = Minecraft.getInstance();

    public static APIPlayerStats getPlayerStats() {
        if (getCurrentStatus()) {
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
        } else return null;
    }

    public static boolean getCurrentStatus() {
        if (mc.player != null) {
            status = "READY";
            return true;
        } else {
            status = "NOT IN WORLD/SERVER";
        }
        return false;
    }

    public static boolean sendChatMessage(String message) {
        if (getCurrentStatus()) {
            assert mc.player != null;
            mc.player.sendChatMessage(message);
            return true;
        }
        return false;
    }

    public static void disconnectFromServer() {
        if (mc.world != null) {
            mc.world.sendQuittingDisconnectingPacket();
        }
        if (mc.getConnection() != null) {
            mc.getConnection().getNetworkManager().handleDisconnection();
        }
    }

    public static void connectToServer(RequestTemplates.ServerConnect server) {
        mc.setServerData(new ServerData("server", server.address, false));
        mc.execute(() -> mc.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), mc, server.address, server.port)));
    }
}
