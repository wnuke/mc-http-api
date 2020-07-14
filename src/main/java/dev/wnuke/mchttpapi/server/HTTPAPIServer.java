package dev.wnuke.mchttpapi.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import dev.wnuke.mchttpapi.utils.MinecraftCompatLayer;
import dev.wnuke.mchttpapi.utils.Pair;
import dev.wnuke.mchttpapi.utils.RequestTemplates;

import java.io.IOException;
import java.net.InetSocketAddress;

import static dev.wnuke.mchttpapi.HeadlessAPI.*;

public class HTTPAPIServer {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final MinecraftCompatLayer mc = new MinecraftCompatLayer();

    public HTTPAPIServer() throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        new JsonGETEndpoint(server, "/chat") {
            @Override
            public Pair<String, Integer> run() {
                return new Pair<>(gson.toJson(chatMessages), null);
            }
        };
        new JsonGETEndpoint(server, "/player") {
            @Override
            public Pair<String, Integer> run() {
                if (mc.isPlayerNotNull()) {
                    return new Pair<>(gson.toJson(mc.getPlayerStats()), null);
                }
                return new Pair<>(null, 500);
            }
        };
        new JsonGETEndpoint(server, "/status") {
            @Override
            public Pair<String, Integer> run() {
                if (mc.isPlayerNotNull()) {
                    status = "PLAYER READY";
                } else {
                    status = "PLAYER NOT READY";
                }
                return new Pair<>(gson.toJson(status), null);
            }
        };
        new JsonPOSTEndpoint(server, "/sendmsg", true) {
            @Override
            public int run(String data) {
                RequestTemplates.ChatMessage chatMessage = gson.fromJson(data, RequestTemplates.ChatMessage.class);
                if (chatMessage.message == null) {
                    return 400;
                } else {
                    if (mc.sendChatMessage(chatMessage.message)) return 200;
                    else return 500;
                }
            }
        };
        new JsonPOSTEndpoint(server, "/connect", true) {
            @Override
            public int run(String data) {
                mc.disconnectFromServer();
                RequestTemplates.ServerConnect serverConnect = gson.fromJson(data, RequestTemplates.ServerConnect.class);
                if (serverConnect == null || serverConnect.address == null) return 400;
                if (serverConnect.port == null) serverConnect.port = 25565;
                if (mc.connectToServer(serverConnect)) {
                    return 200;
                }
                return 500;
            }
        };
        new JsonPOSTEndpoint(server, "/disconnect", false) {
            @Override
            public int run(String data) {
                if (mc.disconnectFromServer()) {
                    return 200;
                }
                return 500;
            }
        };
        new JsonPOSTEndpoint(server, "/posttest", false) {
            @Override
            public int run(String data) {
                System.out.println("Test recieved!");
                return 200;
            }
        };
        new JsonGETEndpoint(server, "/gettest") {
            @Override
            public Pair<String, Integer> run() {
                System.out.println("Checking status...");
                mc.isPlayerNotNull();
                System.out.println("Test recieved!");
                return new Pair<>("Test recieved!", null);
            }
        };
        server.setExecutor(null);
        server.start();
    }
}

