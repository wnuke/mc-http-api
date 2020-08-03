package dev.wnuke.mchttpapi.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import dev.wnuke.mchttpapi.utils.*;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.net.InetSocketAddress;

import static dev.wnuke.mchttpapi.HeadlessAPI.LOGGER;
import static dev.wnuke.mchttpapi.HeadlessAPI.chatMessages;

public enum HTTPAPIServer {
    ;
    public static final Gson gson = new GsonBuilder().serializeNulls().create();
    public static final int PORT = 8000;
    public static final int MCPORT = 25565;

    public static void httpServer(MinecraftCompatLayer compatLayer) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        new JsonGETEndpoint(server, "/chat") {
            @Override
            public Pair<String, Integer> run() {
                LOGGER.info("Sending chat history to ");
                return new Pair<>(gson.toJson(chatMessages), HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            }
        };
        new JsonGETEndpoint(server, "/player") {
            @Override
            public Pair<String, Integer> run() {
                return new Pair<>(gson.toJson(compatLayer.getPlayerStats()), HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            }
        };
        new JsonPOSTEndpoint(server, "/sendmsg", true) {
            @Override
            public int run(String data) {
                ChatMessage chatMessage = gson.fromJson(data, ChatMessage.class);
                if (null == chatMessage.message) {
                    return HttpResponseStatus.BAD_REQUEST.code();
                } else {
                    if (compatLayer.sendChatMessage(chatMessage.message)) return HttpResponseStatus.OK.code();
                    else return HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
                }
            }
        };
        new JsonPOSTEndpoint(server, "/connect", true) {
            @Override
            public int run(String data) {
                compatLayer.disconnectFromServer();
                ServerConnect serverConnect = gson.fromJson(data, ServerConnect.class);
                if (null == serverConnect || null == serverConnect.address)
                    return HttpResponseStatus.BAD_REQUEST.code();
                if (null == serverConnect.port) serverConnect.port = MCPORT;
                if (compatLayer.connectToServer(serverConnect)) {
                    return HttpResponseStatus.OK.code();
                }
                return HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
            }
        };
        new JsonPOSTEndpoint(server, "/login", true) {
            @Override
            public int run(String data) {
                Login loginData = gson.fromJson(data, Login.class);
                if (compatLayer.login(loginData)) {
                    return HttpResponseStatus.OK.code();
                }
                return HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
            }
        };
        new JsonPOSTEndpoint(server, "/disconnect", false) {
            @Override
            public int run(String data) {
                if (compatLayer.disconnectFromServer()) {
                    return HttpResponseStatus.OK.code();
                }
                return HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
            }
        };
        server.setExecutor(null);
        server.start();
    }
}

