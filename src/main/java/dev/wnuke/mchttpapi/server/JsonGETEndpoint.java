package dev.wnuke.mchttpapi.server;

import com.sun.net.httpserver.HttpServer;
import dev.wnuke.mchttpapi.utils.Pair;

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
            logHTTPRequest(he.getRequestURI().getPath(), false);
            if ("GET".equals(he.getRequestMethod())) {
                Pair<String, Integer> result = run();
                if (result.getSecond() != null) {
                    he.sendResponseHeaders(result.getSecond(), -1);
                }
                else if (result.getFirst() != null) {
                    sendOkJsonResponse(result.getFirst(), he);
                }
                else {
                    he.sendResponseHeaders(500, -1);
                }
            } else {
                he.sendResponseHeaders(405, -1);
            }
            logHTTPRequest(he.getRequestURI().getPath(), true);
            he.close();
        }));
    }

    public abstract Pair<String, Integer> run();
}
