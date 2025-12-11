package PoCMonolith;

import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

public class RecommendationService {

    public List<String> getRecommendations() {
        return Arrays.asList(
            "old-recommendation-1",
            "old-recommendation-2",
            "old-recommendation-3"
        );
    }

    public void startHttpContext(HttpServer server) {
        server.createContext("/recommendations", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String response = getRecommendations().toString();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
    }
}
