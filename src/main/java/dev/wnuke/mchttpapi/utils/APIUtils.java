package dev.wnuke.mchttpapi.utils;

import com.sun.net.httpserver.HttpExchange;
import io.netty.handler.codec.http.HttpResponseStatus;

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
            return "";
        }
    }

    public static void logHTTPRequest(HttpExchange he) {
        String uri = he.getRequestURI().getPath();
        String requester = he.getRemoteAddress().getAddress().getHostAddress();
        LOGGER.info("{} requested {}", requester, uri);
    }

    public static void sendOkJsonResponse(String message, HttpExchange httpExchange) throws IOException {
        byte[] messageBytes = (message + "\n").getBytes();
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(HttpResponseStatus.OK.code(), messageBytes.length);
        OutputStream output = httpExchange.getResponseBody();
        output.write(messageBytes);
        output.flush();
    }
}
