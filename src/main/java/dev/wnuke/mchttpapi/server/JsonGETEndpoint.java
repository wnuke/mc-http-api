package dev.wnuke.mchttpapi.server;

import com.sun.net.httpserver.HttpServer;

import java.util.Map;

import static dev.wnuke.mchttpapi.utils.APIUtils.logHTTPRequest;
import static dev.wnuke.mchttpapi.utils.APIUtils.sendOkJsonResponse;

public abstract class JsonGETEndpoint {
    private final HttpServer server;
    private final String path;

    public JsonGETEndpoint(HttpServer server, String path) {
        this.server = server;
        this.path = path;
        createContext();
    }

    public void createContext() {
        server.createContext(path, (he -> {
            logHTTPRequest(he, false);
            if ("GET".equals(he.getRequestMethod())) {
                Map.Entry<String, Integer> result = run();
                if (result.getValue() != null) he.sendResponseHeaders(result.getValue(), -1);
                else if (result.getKey() != null) sendOkJsonResponse(result.getKey(), he);
                else he.sendResponseHeaders(500, -1);
            } else he.sendResponseHeaders(405, -1);
            logHTTPRequest(he, true);
            he.close();
        }));
    }

    abstract Map.Entry<String, Integer> run();
}
