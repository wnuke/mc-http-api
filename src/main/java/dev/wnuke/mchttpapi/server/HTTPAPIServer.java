package dev.wnuke.mchttpapi.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import dev.wnuke.mchttpapi.HeadlessAPI;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import dev.wnuke.mchttpapi.utils.Pair;
import dev.wnuke.mchttpapi.utils.RequestTemplates;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;

import static dev.wnuke.mchttpapi.HeadlessAPI.*;

public class HTTPAPIServer {
    public static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static void httpServer(MinecraftCompatLayer compatLayer) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        new JsonGETEndpoint(server, "/chat") {
            @Override
            public Pair<String, Integer> run() {
                return new Pair<>(gson.toJson(chatMessages), 500);
            }
        };
        new JsonGETEndpoint(server, "/player") {
            @Override
            public Pair<String, Integer> run() {
                return new Pair<>(gson.toJson(compatLayer.getPlayerStats()), 500);
            }
        };
        new JsonPOSTEndpoint(server, "/sendmsg", true) {
            @Override
            public int run(String data) {
                RequestTemplates.ChatMessage chatMessage = gson.fromJson(data, RequestTemplates.ChatMessage.class);
                if (chatMessage.message == null) {
                    return 400;
                } else {
                    if (compatLayer.sendChatMessage(chatMessage.message)) return 200;
                    else return 500;
                }
            }
        };
        new JsonPOSTEndpoint(server, "/connect", true) {
            @Override
            public int run(String data) {
                compatLayer.disconnectFromServer();
                RequestTemplates.ServerConnect serverConnect = gson.fromJson(data, RequestTemplates.ServerConnect.class);
                if (serverConnect == null || serverConnect.address == null) return 400;
                if (serverConnect.port == null) serverConnect.port = 25565;
                if (compatLayer.connectToServer(serverConnect)) {
                    return 200;
                }
                return 500;
            }
        };
        new JsonPOSTEndpoint(server, "/login", true) {
            @Override
            public int run(String data) {
                RequestTemplates.Login loginData = gson.fromJson(data, RequestTemplates.Login.class);
                if (compatLayer.login(loginData)) {
                    return 200;
                }
                return 500;
            }
        };
        new JsonPOSTEndpoint(server, "/disconnect", false) {
            @Override
            public int run(String data) {
                if (compatLayer.disconnectFromServer()) {
                    return 200;
                }
                return 500;
            }
        };
        server.setExecutor(null);
        server.start();
    }
}

