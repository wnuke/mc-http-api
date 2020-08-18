package dev.wnuke.mchttpapi;


import dev.wnuke.mchttpapi.server.HTTPAPIServer;
import dev.wnuke.mchttpapi.utils.Login;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class HeadlessAPI implements ModInitializer {
    public static final ArrayList<String> chatMessages = new ArrayList<>(0);
    public static final Logger LOGGER = LogManager.getLogger();
    public static MinecraftCompatLayer compatLayer;
    protected static APIServerThread api;
    public static Login startUser;

    public static void startAPIServer(MinecraftCompatLayer compatLayerToUse) {
        api = APIServerThread.createAPIServerThread(compatLayerToUse);
        api.start();
    }

    public void onInitialize() {
        LOGGER.info("Loading HeadlessAPI v1.0.0 by wnuke...");
        compatLayer = new MinecraftCompatLayer(MinecraftClient.getInstance());
        startUser = new Login();
        startUser.username = randomUsername();
        compatLayer.login(startUser);
        startAPIServer(compatLayer);
        LOGGER.info("---------------------------------");
        LOGGER.info("*                               *");
        LOGGER.info("*   Headless HTTP API loaded!   *");
        LOGGER.info("*                               *");
        LOGGER.info("---------------------------------");
    }

    public static String randomUsername() {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST".toCharArray();

        StringBuilder randomString = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; 8 > i; i++) {
            char c = chars[random.nextInt(chars.length)];
            randomString.append(c);
        }

        return randomString.toString();
    }

    public static class APIServerThread extends Thread {
        public MinecraftCompatLayer minecraftCompatLayer;

        private APIServerThread(MinecraftCompatLayer mcCompatLayer) {
            setName("HTTP-API");
            minecraftCompatLayer = mcCompatLayer;
        }

        public static APIServerThread createAPIServerThread(MinecraftCompatLayer compatLayer) {
            return new APIServerThread(compatLayer);
        }

        @Override
        public void run() {
            try {
                HTTPAPIServer.httpServer(minecraftCompatLayer);
            } catch (IOException ioException) {
                LOGGER.error(ioException.getLocalizedMessage());
            }
        }

        @Override
        public String toString() {
            return "APIServerThread{" +
                    "minecraftCompatLayer=" + minecraftCompatLayer +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HeadlessAPI{" +
                "startUser=" + startUser +
                '}';
    }
}


