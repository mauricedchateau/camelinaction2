package camelinaction;

import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example how to use JSON data format with the camel-jackson component.
 * <p/>
 * We use camel-jetty to expose a HTTP service which returns the JSON response.
 */
public class PurchaseOrderJSONTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderJSONTest.class);

    @Override
    protected void bindToRegistry(Registry registry) throws Exception {
        registry.bind("orderService", new OrderServiceBean());
    }

    @Test
    public void testJSON() throws Exception {
        String out = template.requestBody("rest:get:order/service?id=123&host=localhost:8080", null, String.class);
        LOG.info("Response from order service: {}", out);

        assertNotNull(out);
        assertTrue(out.contains("Camel in Action"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty://http://0.0.0.0:8080/order/service")
                    .bean("orderService", "lookup")
                    .marshal().json(JsonLibrary.Jackson);
            }
        };
    }

    public static class OrderServiceBean {

        public PurchaseOrder lookup(@Header("id") String id) {
            LOG.info("Finding purchase order for id {}", id);
            // just return a fixed response
            PurchaseOrder order = new PurchaseOrder();
            order.setPrice(69.99);
            order.setAmount(1);
            order.setName("Camel in Action");
            return order;
        }

    }

}


