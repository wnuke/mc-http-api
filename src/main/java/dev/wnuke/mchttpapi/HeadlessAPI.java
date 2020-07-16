package dev.wnuke.mchttpapi;


import java.io.IOException;
import java.util.ArrayList;
import dev.wnuke.mchttpapi.server.HTTPAPIServer;
import net.minecraft.client.Minecraft;

public class HeadlessAPI {
    public static ArrayList<String> chatMessages = new ArrayList<>();
    protected static APIServerThread api;
    public static String status;
    public static Minecraft minecraft;

    public HeadlessAPI(Minecraft minecraft) {
        HeadlessAPI.minecraft = minecraft;
        startAPIServer();
    }

    public void startAPIServer() {
        api = new APIServerThread();
        api.start();
        System.out.println("---------------------------------");
        System.out.println("*                               *");
        System.out.println("*   Headless HTTP API loaded!   *");
        System.out.println("*                               *");
        System.out.println("---------------------------------");
    }

    public static class APIServerThread extends Thread {
        public HTTPAPIServer server;

        @Override
        public void run() {
            try {
                server = new HTTPAPIServer();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}


