package dev.wnuke.mchttpapi.server;

import com.sun.net.httpserver.HttpServer;
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
            logHTTPRequest(he);
            if ("GET".equals(he.getRequestMethod())) {
                sendOkJsonResponse(run(), he);
            } else {
                he.sendResponseHeaders(HttpResponseStatus.METHOD_NOT_ALLOWED.code(), -1L);
            }
            he.close();
        }));
    }

    public abstract String run();

    @Override
    public String toString() {
        return "JsonGETEndpoint{" +
                "server=" + server +
                ", path='" + path + '\'' +
                '}';
    }
}
