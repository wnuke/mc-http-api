package dev.wnuke.mchttpapi.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.AbstractMap;
import java.util.Map;

import static dev.wnuke.mchttpapi.HeadlessAPI.*;
import static dev.wnuke.mchttpapi.MinecraftCompatLayer.*;

public class HTTPAPIServer {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public HTTPAPIServer() throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        new JsonGETEndpoint(server, "/chat") {
            @Override
            Map.Entry<String, Integer> run() {
                getCurrentStatus();
                return new AbstractMap.SimpleEntry<>(gson.toJson(chatMessages), null);
            }
        };
        new JsonGETEndpoint(server, "/player") {
            @Override
            Map.Entry<String, Integer> run() {
                getCurrentStatus();
                if (getCurrentStatus()) {
                    return new AbstractMap.SimpleEntry<>(gson.toJson(getPlayerStats()), null);
                }
                return new AbstractMap.SimpleEntry<>(null, 500);
            }
        };
        new JsonGETEndpoint(server, "/status") {
            @Override
            Map.Entry<String, Integer> run() {
                getCurrentStatus();
                return new AbstractMap.SimpleEntry<>(gson.toJson(status), null);
            }
        };
        new JsonPOSTEndpoint(server, "/sendmsg", true) {
            @Override
            int run(String data) {
                RequestTemplates.ChatMessage chatMessage = gson.fromJson(data, RequestTemplates.ChatMessage.class);
                System.out.println("Message: " + chatMessage.message);
                if (chatMessage.message == null) {
                    return 400;
                } else {
                    if (sendChatMessage(chatMessage.message)) return 200;
                    else return 500;
                }
            }
        };
        new JsonPOSTEndpoint(server, "/connect", true) {
            @Override
            int run(String data) {
                disconnectFromServer();
                RequestTemplates.ServerConnect serverConnect = gson.fromJson(data, RequestTemplates.ServerConnect.class);
                if (serverConnect == null || serverConnect.address == null) return 400;
                if (serverConnect.port == null) serverConnect.port = 25565;
                System.out.println("Connecting to " + serverConnect.address + " on port: " + serverConnect.port + "...");
                connectToServer(serverConnect);
                return 200;
            }
        };
        new JsonPOSTEndpoint(server, "/disconnect", false) {
            @Override
            int run(String data) {
                if (disconnectFromServer()) return 200;
                return 500;
            }
        };
        server.setExecutor(null);
        server.start();
    }
}

