package dev.wnuke.mchttpapi.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static dev.wnuke.mchttpapi.HeadlessAPI.LOGGER;

public enum APIUtils {
    ;
    public static String parsePost(HttpExchange he) {
        try {
            InputStream input = he.getRequestBody();
            StringBuilder sb = new StringBuilder();
            int i = input.read();
            while (-1L != i) {
                sb.append((char) i);
                i = input.read();
            }
            return sb.toString();
        } catch (IOException e) {
            LOGGER.warn(e.getLocalizedMessage());
            return null;
        }
    }

    public static void logHTTPRequest(HttpExchange he, boolean end) {
        String uri = he.getRequestURI().getPath();
        String requester = he.getRemoteAddress().getAddress().getHostAddress() + ":" + he.getRemoteAddress().getPort();
        if (end) LOGGER.info("HTTP Connection opened by {} to {} has been closed.", requester, uri);
        else LOGGER.info("HTTP Connection opened by {} to {}.", requester, uri);
    }

    public static void sendOkJsonResponse(String message, HttpExchange httpExchange) throws IOException {
        byte[] messageBytes = (message + "\n").getBytes();
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, messageBytes.length);
        OutputStream output = httpExchange.getResponseBody();
        output.write(messageBytes);
        output.flush();
    }
}
