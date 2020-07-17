package dev.wnuke.mchttpapi;


import com.google.gson.reflect.TypeToken;
import dev.wnuke.mchttpapi.server.HTTPAPIServer;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HeadlessAPI implements ModInitializer {
    public static ArrayList<String> chatMessages = new ArrayList<>();
    public static String status;
    public static Boolean disableRender;
    public static final File configFile = new File("APIconfig.json");
    protected static APIServerThread api;
    public static MinecraftCompatLayer compatLayer;

    public void onInitialize() {
        System.out.println("Loading HeadlessAPI v1.0.0 by wnuke...");
        try {
            configFile.createNewFile();
            FileReader fileReader = new FileReader(configFile);
            disableRender = HTTPAPIServer.gson.fromJson(fileReader, Boolean.class);
            if (disableRender == null) disableRender = false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load config, continuing.");
        }
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


