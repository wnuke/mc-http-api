package dev.wnuke.mchttpapi.utils;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Session;
import net.minecraft.realms.RealmsBridge;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.UUID;


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
            minecraft.execute(() -> {
                minecraft.disconnect();
            });
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

    public static boolean setGameSession(Session newSession) {
        Class<? extends MinecraftClient> minecraftClientClass = minecraft.getClass();
        if (minecraftClientClass == null) return false;
        Field sessionField = null;
        for (Field field : minecraftClientClass.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("session") || field.getName().equalsIgnoreCase("field_1726")) {
                sessionField = field;
            }
        }
        try {
            if (sessionField == null) return false;
            sessionField.setAccessible(true);
            sessionField.set(minecraft, newSession);
            sessionField.setAccessible(false);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(RequestTemplates.Login loginData) {
        try {
            if (loginData.username == null || loginData.username.isEmpty()) {
                return false;
            } else if (loginData.password == null || loginData.password.isEmpty()) {
                Session offlineSession = new Session(loginData.username,
                        UUID.nameUUIDFromBytes(loginData.username.getBytes()).toString(), "0", "legacy");
                return setGameSession(offlineSession);
            } else {
                YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                auth.setUsername(loginData.username);
                auth.setPassword(loginData.password);
                try {
                    auth.logIn();
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                    return false;
                }
                Session onlineSession = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
                return setGameSession(onlineSession);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
