package PoCMonolith;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws Exception {
        // Start HTTP server on port 8080
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Initialize services
        SearchService searchService = new SearchService();
        RecommendationService recommendationService = new RecommendationService();

        // Attach HTTP endpoints
        searchService.startHttpContext(server);
        recommendationService.startHttpContext(server);

        server.setExecutor(null);
        server.start();

        System.out.println("HTTP server started at http://localhost:" + port);
        System.out.println("Endpoints: /search, /recommendations");
    }
}
