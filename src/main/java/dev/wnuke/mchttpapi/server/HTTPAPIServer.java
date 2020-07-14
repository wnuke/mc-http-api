package dev.wnuke.mchttpapi.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import dev.wnuke.mchttpapi.utils.Pair;
import dev.wnuke.mchttpapi.utils.RequestTemplates;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.net.InetSocketAddress;

import static dev.wnuke.mchttpapi.HeadlessAPI.*;
import static dev.wnuke.mchttpapi.utils.MinecraftCompatLayer.*;

public class HTTPAPIServer {
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public HTTPAPIServer() throws IOException {
        System.out.println(Minecraft.getInstance().player);
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        new JsonGETEndpoint(server, "/chat") {
            @Override
            public Pair<String, Integer> run() {
                getCurrentStatus();
                return new Pair<>(gson.toJson(chatMessages), null);
            }
        };
        new JsonGETEndpoint(server, "/player") {
            @Override
            public Pair<String, Integer> run() {
                if (getCurrentStatus()) {
                    return new Pair<>(gson.toJson(getPlayerStats()), null);
                }
                return new Pair<>(null, 500);
            }
        };
        new JsonGETEndpoint(server, "/status") {
            @Override
            public Pair<String, Integer> run() {
                getCurrentStatus();
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
                    if (sendChatMessage(chatMessage.message)) return 200;
                    else return 500;
                }
            }
        };
        new JsonPOSTEndpoint(server, "/connect", true) {
            @Override
            public int run(String data) {
                disconnectFromServer();
                RequestTemplates.ServerConnect serverConnect = gson.fromJson(data, RequestTemplates.ServerConnect.class);
                if (serverConnect == null || serverConnect.address == null) return 400;
                if (serverConnect.port == null) serverConnect.port = 25565;
                connectToServer(serverConnect);
                return 200;
            }
        };
        new JsonPOSTEndpoint(server, "/disconnect", false) {
            @Override
            public int run(String data) {
                disconnectFromServer();
                return 200;
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
                System.out.println("Test recieved!");
                return new Pair<>("Test recieved!", null);
            }
        };
        server.setExecutor(null);
        server.start();
    }
}

