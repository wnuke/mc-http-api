package dev.wnuke.mchttpapi.server;

import com.sun.net.httpserver.HttpServer;
import dev.wnuke.mchttpapi.utils.Pair;
import io.netty.handler.codec.http.HttpResponseStatus;

import static dev.wnuke.mchttpapi.utils.APIUtils.logHTTPRequest;
import static dev.wnuke.mchttpapi.utils.APIUtils.sendOkJsonResponse;

public abstract class JsonGETEndpoint {
    private final HttpServer server;
    private final String path;

    public JsonGETEndpoint(HttpServer httpServer, String endpointPath) {
        server = httpServer;
        path = endpointPath;
        createContext();
    }

    public void createContext() {
        server.createContext(path, (he -> {
            logHTTPRequest(he, false);
            if ("GET".equals(he.getRequestMethod())) {
                Pair<String, Integer> result = run();
                if (result.getFirst().isEmpty()) {
                    he.sendResponseHeaders(result.getSecond(), -1L);
                } else {
                    sendOkJsonResponse(result.getFirst(), he);
                }
            } else {
                he.sendResponseHeaders(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), -1L);
            }
            logHTTPRequest(he, true);
            he.close();
        }));
    }

    public abstract Pair<String, Integer> run();

    @Override
    public String toString() {
        return "JsonGETEndpoint{" +
                "server=" + server +
                ", path='" + path + '\'' +
                '}';
    }
}
