package dev.wnuke.mchttpapi.server;

import com.sun.net.httpserver.HttpServer;
import io.netty.handler.codec.http.HttpResponseStatus;

import static dev.wnuke.mchttpapi.utils.APIUtils.logHTTPRequest;
import static dev.wnuke.mchttpapi.utils.APIUtils.parsePost;

public abstract class JsonPOSTEndpoint {
    private final HttpServer server;
    private final String path;

    public JsonPOSTEndpoint(HttpServer httpServer, String requestPath, boolean needsData) {
        server = httpServer;
        path = requestPath;
        createContext(needsData);
    }

    public void createContext(boolean needsData) {
        server.createContext(path, (he -> {
            logHTTPRequest(he);
            if ("POST".equals(he.getRequestMethod())) {
                if (needsData) {
                    if (he.getRequestHeaders().containsKey("Content-Type")) {
                        if ("application/json".equals(he.getRequestHeaders().get("Content-Type").get(0))) {
                            he.sendResponseHeaders(run(parsePost(he)), -1L);
                        } else he.sendResponseHeaders(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE.code(), -1L);
                    } else he.sendResponseHeaders(HttpResponseStatus.BAD_REQUEST.code(), -1L);
                } else he.sendResponseHeaders(run(""), -1L);
            } else {
                he.sendResponseHeaders(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), -1L);
            }
            he.close();
        }));
    }

    public abstract int run(String data);

    @Override
    public String toString() {
        return "JsonPOSTEndpoint{" +
                "server=" + server +
                ", path='" + path + '\'' +
                '}';
    }
}
