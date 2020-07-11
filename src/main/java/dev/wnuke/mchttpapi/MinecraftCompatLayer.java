package dev.wnuke.mchttpapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.multiplayer.ServerData;
import dev.wnuke.mchttpapi.utils.RequestTemplates;
import dev.wnuke.mchttpapi.utils.APIPlayerStats;

import static dev.wnuke.mchttpapi.HeadlessAPI.*;


public class MinecraftCompatLayer {
    private static final Minecraft mc = Minecraft.getInstance();
    public static boolean playerNotNull() {
        return mc.player != null;
    }
    public static APIPlayerStats getPlayerStats() {
        if (playerNotNull()) {
            APIPlayerStats stats = new APIPlayerStats();
            stats.name = mc.player.getName().getString();
            stats.uuid = mc.player.getUniqueID().toString();
            stats.player = new APIPlayerStats.PlayerInfo();
            stats.player.health = mc.player.getHealth();
            stats.player.hunger = mc.player.getFoodStats().getFoodLevel();
            stats.player.saturation = mc.player.getFoodStats().getSaturationLevel();
            stats.coordinates = new APIPlayerStats.Position(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
            return stats;
        }
        else return null;
    }
    public static boolean getCurrentStatus() {
        if (mc.currentScreen instanceof ConnectingScreen) status = "CONNECTING TO SERVER";
        else if (!playerNotNull()) status = "NOT IN SERVER";
        else {
            status = "READY";
            return true;
        }
        return false;
    }
    public static boolean sendChatMessage(String message) {
        if (playerNotNull()) {
            mc.player.sendChatMessage(message);
            return true;
        }
        return false;
    }
    public static boolean disconnectFromServer() {
        if (playerNotNull()) {
            if (mc.world != null) {
                mc.world.sendQuittingDisconnectingPacket();
            }
            if (mc.getConnection() != null) {
                mc.getConnection().getNetworkManager().handleDisconnection();
            }
            return true;
        }
        return false;
    }
    public static void connectToServer(RequestTemplates.ServerConnect server) {
        mc.setServerData(new ServerData("server", server.address, false));
        mc.execute(() -> mc.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), mc, server.address, server.port)));
    }
}
