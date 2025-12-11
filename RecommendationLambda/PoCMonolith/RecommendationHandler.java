package PoCMonolith;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecommendationHandler implements RequestHandler<Map<String, Object>, List<String>> {

    @Override
    public List<String> handleRequest(Map<String, Object> input, Context context) {
        context.getLogger().log("Received input: " + input);

        // New recommendations
        List<String> recommendations = Arrays.asList(
            "new-recommendation-1",
            "new-recommendation-2",
            "new-recommendation-3"
        );

        return recommendations; // ✅ return as List<String>
    }
}
