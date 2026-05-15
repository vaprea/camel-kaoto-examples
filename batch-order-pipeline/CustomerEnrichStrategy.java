//DEPS org.apache.camel:camel-quartz:RELEASE

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles customer data merging in two contexts:
 *
 * 1. aggregate() — AggregationStrategy interface, unused by the current route
 *    but kept for completeness if the enrich strategy is re-introduced.
 *
 * 2. mergeWithOriginal() — called as a bean step inside direct:db-lookup.
 *    At that point the exchange holds:
 *      - property "originalOrder" = the order Map saved before the SQL call
 *      - body = the customer Map returned by the SQL query
 *    The method merges both and returns the combined Map as the new body,
 *    so that enrich (with default SetBodyAggregationStrategy) picks it up correctly.
 */
public class CustomerEnrichStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        if (original == null) return resource;
        merge(original.getMessage().getBody(Map.class),
              resource != null ? resource.getMessage().getBody(Map.class) : null);
        return original;
    }

    /** Called from the bean step in direct:db-lookup. */
    public Map<String, Object> mergeWithOriginal(Exchange exchange) {
        @SuppressWarnings("unchecked")
        Map<String, Object> order = new LinkedHashMap<>(
            exchange.getProperty("originalOrder", Map.class)
        );
        @SuppressWarnings("unchecked")
        Map<String, Object> customer = exchange.getMessage().getBody(Map.class);
        merge(order, customer);
        return order;
    }

    private void merge(Map<String, Object> order, Map<String, Object> customer) {
        if (order == null || customer == null) return;
        order.put("customer_name",  customer.getOrDefault("customer_name",  "Unknown"));
        order.put("customer_email", customer.getOrDefault("customer_email", "N/A"));
        order.put("tier",           customer.getOrDefault("tier",           "standard"));
    }
}
