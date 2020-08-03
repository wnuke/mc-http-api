package dev.wnuke.mchttpapi.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static dev.wnuke.mchttpapi.HeadlessAPI.LOGGER;

public class APIUtils {

    public static String parsePost(HttpExchange he) {
        try {
            InputStream input = he.getRequestBody();
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = input.read()) != -1) {
                sb.append((char) i);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void logHTTPRequest(HttpExchange he, boolean end) {
        String uri = he.getRequestURI().getPath();
        String requester = he.getRemoteAddress().getAddress().getHostAddress() + ":" + he.getRemoteAddress().getPort();
        if (end) LOGGER.info("HTTP Exchanged with URI \"{}\" opened by {} has been closed.", uri, requester);
        else LOGGER.info("HTTP Exchanged opened with URI \"{}\" opened by {}.", uri, requester);
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
