package dev.wnuke.mchttpapi;


import dev.wnuke.mchttpapi.server.HTTPAPIServer;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.util.ArrayList;

public class HeadlessAPI implements ModInitializer {
    public static ArrayList<String> chatMessages = new ArrayList<>();
    public static String status;
    protected static APIServerThread api;
    public static MinecraftCompatLayer compatLayer;

    public void onInitialize() {
        compatLayer = new MinecraftCompatLayer(MinecraftClient.getInstance());
        startAPIServer(compatLayer);
    }

    public static void startAPIServer(MinecraftCompatLayer compatLayer) {
        api = new APIServerThread(compatLayer);
        api.start();
        System.out.println("---------------------------------");
        System.out.println("*                               *");
        System.out.println("*   Headless HTTP API loaded!   *");
        System.out.println("*                               *");
        System.out.println("---------------------------------");
    }

    public static class APIServerThread extends Thread {
        public HTTPAPIServer server;

        public MinecraftCompatLayer compatLayer;

        public APIServerThread(MinecraftCompatLayer compatLayer) {
            this.setName("HTTP-API");
            this.compatLayer = compatLayer;
        }

        @Override
        public void run() {
            try {
                server = new HTTPAPIServer(compatLayer);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}


