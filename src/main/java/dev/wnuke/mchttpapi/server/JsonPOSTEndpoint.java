package dev.wnuke.mchttpapi.server;

import com.sun.net.httpserver.HttpServer;

import static dev.wnuke.mchttpapi.utils.APIUtils.logHTTPRequest;
import static dev.wnuke.mchttpapi.utils.APIUtils.parsePost;

public abstract class JsonPOSTEndpoint {
    private final HttpServer server;
    private final String path;

    public JsonPOSTEndpoint(HttpServer server, String path, boolean needsData) {
        this.server = server;
        this.path = path;
        createContext(needsData);
    }

    public void createContext(boolean needsData) {
        server.createContext(path, (he -> {
            logHTTPRequest(he.getRequestURI().getPath(), false);
            if ("POST".equals(he.getRequestMethod())) {
                if (needsData) {
                    if (he.getRequestHeaders().containsKey("Content-Type")) {
                        if (he.getRequestHeaders().get("Content-Type").get(0).equals("application/json")) {
                            he.sendResponseHeaders(run(parsePost(he)), -1);
                        } else he.sendResponseHeaders(415, -1);
                    } else he.sendResponseHeaders(400, -1);
                } else he.sendResponseHeaders(run(""), -1);
            } else {
                he.sendResponseHeaders(405, -1);
            }
            logHTTPRequest(he.getRequestURI().getPath(), true);
            he.close();
        }));
    }

    public abstract int run(String data);
}
