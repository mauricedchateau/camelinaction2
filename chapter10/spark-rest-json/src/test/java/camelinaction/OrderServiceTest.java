package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.util.StringHelper;
import org.junit.jupiter.api.Test;

public class OrderServiceTest extends CamelTestSupport {

    // use dummy service for testing purpose
    private OrderService orderService = new DummyOrderService();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("orderService", orderService);
        return jndi;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new OrderRoute();
    }

    @Test
    public void testCreateOrder() throws Exception {
        String json = "{\"partName\":\"motor\",\"amount\":1,\"customerName\":\"honda\"}";

        log.info("Sending order using json payload: {}", json);

        // use http component to send the order
        String id = template.requestBody("http://localhost:8080/orders", json, String.class);
        assertNotNull(id);

        log.info("Created new order with id {}", id);

        // should create a new order with id 3 (json format so its enclosed in quotes)
        assertEquals("\"3\"", id);
    }

    @Test
    public void testCreateAndGetOrder() throws Exception {
        String json = "{\"partName\":\"motor\",\"amount\":1,\"customerName\":\"honda\"}";

        log.info("Sending order using json payload: {}", json);

        // use http component to send the order
        String id = template.requestBody("http://localhost:8080/orders", json, String.class);
        assertNotNull(id);

        log.info("Created new order with id {}", id);

        // should create a new order with id 3 (json format so its enclosed in quotes)
        assertEquals("\"3\"", id);

        // remove quoutes
        id = StringHelper.removeQuotes(id);

        // use http component to get the order
        String response = template.requestBody("http://localhost:8080/orders/" + id, null, String.class);
        log.info("Response: {}", response);
    }

}
