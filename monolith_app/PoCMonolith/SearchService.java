package PoCMonolith;

import java.util.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

public class SearchService {

    public List<String> getSearchResults() {
        return Arrays.asList("search1", "search2", "search3");
    }

    public void startHttpContext(HttpServer server) {
        server.createContext("/search", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            String response = getSearchResults().toString();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
    }
}
