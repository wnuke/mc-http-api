package dev.wnuke.mchttpapi;


import dev.wnuke.mchttpapi.server.HTTPAPIServer;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HeadlessAPI implements ModInitializer {
    public static ArrayList<String> chatMessages = new ArrayList<>();
    protected static APIServerThread api;
    public static MinecraftCompatLayer compatLayer;

    public void onInitialize() {
        System.out.println("Loading HeadlessAPI v1.0.0 by wnuke...");
        compatLayer = new MinecraftCompatLayer(MinecraftClient.getInstance());
        startAPIServer(compatLayer);
        System.out.println("---------------------------------");
        System.out.println("*                               *");
        System.out.println("*   Headless HTTP API loaded!   *");
        System.out.println("*                               *");
        System.out.println("---------------------------------");
    }

    public static void startAPIServer(MinecraftCompatLayer compatLayer) {
        api = new APIServerThread(compatLayer);
        api.start();
    }

    public static class APIServerThread extends Thread {
        public MinecraftCompatLayer compatLayer;

        public APIServerThread(MinecraftCompatLayer compatLayer) {
            this.setName("HTTP-API");
            this.compatLayer = compatLayer;
        }

        @Override
        public void run() {
            try {
                HTTPAPIServer.httpServer(compatLayer);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}


