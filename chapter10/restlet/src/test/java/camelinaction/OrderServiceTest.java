package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit5.CamelTestSupport;
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
        Order order = new Order();
        order.setAmount(1);
        order.setPartName("motor");
        order.setCustomerName("honda");

        // convert to XML which we support
        String xml = context.getTypeConverter().convertTo(String.class, order);

        log.info("Sending order using xml payload: {}", xml);

        // use restlet component to send the order as xml payload
        String id = template.requestBodyAndHeader("restlet:http://0.0.0.0:8080/orders?restletMethod=POST", xml, Exchange.CONTENT_TYPE, "application/xml", String.class);
        assertNotNull(id);

        log.info("Created new order with id {}", id);

        // should create a new order with id 1
        assertEquals("1", id);
    }

    @Test
    public void testCreateAndGetOrder() throws Exception {
        Order order = new Order();
        order.setAmount(1);
        order.setPartName("motor");
        order.setCustomerName("honda");

        // convert to XML which we support
        String xml = context.getTypeConverter().convertTo(String.class, order);

        log.info("Sending order using xml payload: {}", xml);

        // use restlet component to send the order
        String id = template.requestBodyAndHeader("restlet:http://0.0.0.0:8080/orders?restletMethod=POST", xml, Exchange.CONTENT_TYPE, "application/xmll", String.class);
        assertNotNull(id);

        log.info("Created new order with id {}", id);

        // should create a new order with id 1
        assertEquals("1", id);

        // use restlet component to get the order
        String response = template.requestBodyAndHeader("restlet:http://0.0.0.0:8080/orders/{id}?restletMethod=GET", null, "id", "1", String.class);
        log.info("Response: {}", response);
    }

}
