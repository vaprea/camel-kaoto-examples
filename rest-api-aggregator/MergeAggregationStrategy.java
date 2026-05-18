//DEPS org.apache.camel:camel-jackson:RELEASE

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Scatter-Gather aggregation: collects one JSON response per microservice and
 * builds a single combined object keyed by the ServiceName header.
 *
 * Expected header on each incoming exchange: ServiceName = "products" | "prices" | "stock"
 */
public class MergeAggregationStrategy implements AggregationStrategy {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        try {
            String serviceName = newExchange.getIn().getHeader("ServiceName", String.class);
            String responseBody = newExchange.getIn().getBody(String.class);

            ObjectNode merged;
            Exchange result;

            if (oldExchange == null) {
                merged = MAPPER.createObjectNode();
                result = newExchange;
            } else {
                merged = (ObjectNode) MAPPER.readTree(oldExchange.getIn().getBody(String.class));
                result = oldExchange;
            }

            merged.set(serviceName, MAPPER.readTree(responseBody));
            result.getIn().setBody(merged.toString());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to merge service responses", e);
        }
    }
}
