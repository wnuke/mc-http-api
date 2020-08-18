package dev.wnuke.mchttpapi.utils;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.logging.UncaughtExceptionLogger;

import java.net.InetAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.wnuke.mchttpapi.HeadlessAPI.LOGGER;
import static dev.wnuke.mchttpapi.HeadlessAPI.startUser;


public class MinecraftCompatLayer {
    private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
    private static final Pattern UNREGEX = Pattern.compile("[^A-Za-z0-9]", Pattern.LITERAL);
    public PropertyMap sessionProperties = new PropertyMap();
    public ClientConnection connection;
    private MinecraftClient minecraft;
    public Session session;

    public MinecraftCompatLayer(MinecraftClient mc) {
        minecraft = mc;
    }

    public boolean playerNotNull() {
        if (null != minecraft.player) {
            if (minecraft.player.isDead()) {
                respawn();
            }
            return true;
        }
        return false;
    }

    public void respawn() {
        minecraft.execute(() -> {
            assert null != minecraft.player;
            minecraft.player.requestRespawn();
            minecraft.openScreen(null);
        });
    }

    public APIPlayerStats getPlayerStats() {
        APIPlayerStats stats = new APIPlayerStats();
        try {
            if (playerNotNull()) {
                assert null != minecraft.player;
                stats.name = minecraft.player.getName().asString();
                stats.uuid = minecraft.player.getUuidAsString();
                stats.player = new APIPlayerStats.PlayerInfo();
                stats.player.health = minecraft.player.getHealth();
                stats.player.hunger = minecraft.player.getHungerManager().getFoodLevel();
                stats.player.saturation = minecraft.player.getHungerManager().getSaturationLevel();
                stats.coordinates = new APIPlayerStats.Position(minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ());
            }
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage());
        }
        return stats;
    }

    public boolean sendChatMessage(String message) {
        try {
            if (null != minecraft.player) {
                LOGGER.info("Sending message \"{}\" as \"{}\"", message, minecraft.player.getDisplayName());
                connection.send(new ChatMessageC2SPacket(message));
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage());
        }
        return false;
    }

    public boolean disconnectFromServer() {
        try {
            LOGGER.info("Disconnecting from server {}", null != minecraft.getCurrentServerEntry() ? minecraft.getCurrentServerEntry().address : "");
            disconnect();
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage());
        }
        return false;
    }

    public void disconnect() {
        if (null != minecraft.getNetworkHandler()) {
            minecraft.getNetworkHandler().clearWorld();
        }
        minecraft.gameRenderer.reset();
        NarratorManager.INSTANCE.clear();
        minecraft.inGameHud.clear();
        minecraft.interactionManager = null;
        NarratorManager.INSTANCE.clear();
        minecraft.setCurrentServerEntry(null);
        minecraft.getGame().onLeaveGameSession();
        minecraft.world = null;
        minecraft.player = null;
    }

    public boolean connectToServer(ServerConnect server) {
        try {
            disconnect();
            minecraft.setCurrentServerEntry(new ServerInfo("server", server.address, false));
            String address = server.address;
            Integer port = server.port;
            Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()) {
                public void run() {
                    try {
                        LOGGER.info("Connecting to {}:{}.", address, port);
                        InetAddress inetAddress = InetAddress.getByName(address);
                        connection = ClientConnection.connect(inetAddress, port, true);
                        connection.setPacketListener(new ClientLoginNetworkHandler(connection, minecraft, null, (text) -> {
                        }));
                        connection.send(new HandshakeC2SPacket(address, port, NetworkState.LOGIN));
                        connection.send(new LoginHelloC2SPacket(session.getProfile()));
                        LOGGER.info("Connected.");
                    } catch (UnknownHostException e) {
                        LOGGER.error("Connection to server failed. (Unknown Host)");
                        disconnect();
                    } catch (Exception e) {
                        LOGGER.error("Connection to server failed.", e);
                        disconnect();
                    }
                }
            };
            thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
            thread.start();
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage());
        }
        return false;
    }

    public boolean login(Login loginData) {
        try {
            if (null == loginData.username || loginData.username.isEmpty()) {
                return false;
            } else if (null == loginData.password || loginData.password.isEmpty()) {
                session = new Session(loginData.username,
                        UUID.nameUUIDFromBytes(loginData.username.getBytes()).toString(), "0", "legacy");
                return true;
            } else {
                UserAuthentication auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                auth.setUsername(loginData.username);
                auth.setPassword(loginData.password);
                try {
                    auth.logIn();
                } catch (AuthenticationException e) {
                    LOGGER.warn("Online login failed, logging in offline.");
                    Login offlineLogin = new Login();
                    offlineLogin.username = UNREGEX.matcher(loginData.username).replaceAll(Matcher.quoteReplacement(""));
                    offlineLogin.password = "";
                    login(offlineLogin);
                    return false;
                }
                session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage());
        }
        return false;
    }

    public void logout() {
        disconnectFromServer();
        login(startUser);
    }

    @Override
    public String toString() {
        return "MinecraftCompatLayer{" +
                "sessionProperties=" + sessionProperties +
                ", connection=" + connection +
                ", minecraft=" + minecraft +
                ", session=" + session +
                '}';
    }
}
