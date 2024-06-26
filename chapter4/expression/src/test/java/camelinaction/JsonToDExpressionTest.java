package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.junit5.TestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test to demonstrate using bean as expressions during routing
 */
public class JsonToDExpressionTest extends CamelTestSupport {

    @BeforeEach
    public void setUp() throws Exception {
        TestSupport.deleteDirectory("target/order");
        super.setUp();
    }

    @Test
    public void sendUSOrder() throws Exception {
        // we expect the order to be from a US customer accordingly to the rules in CustomerService bean

        getMockEndpoint("mock:queue:US").expectedMessageCount(1);
        getMockEndpoint("mock:queue:EMEA").expectedMessageCount(0);
        getMockEndpoint("mock:queue:OTHER").expectedMessageCount(0);

        // prepare a JSon document from a String
        String json = "{ \"order\": { \"customerId\": 88, \"item\": \"ActiveMQ in Action\" } }";

        // store the order as a file which is picked up by the route
        template.sendBodyAndHeader("file://target/order", json, Exchange.FILE_NAME, "order.json");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    public void sendEMEAOrder() throws Exception {
        // we expect the order to be from a EMEA customer accordingly to the rules in CustomerService bean

        getMockEndpoint("mock:queue:US").expectedMessageCount(0);
        getMockEndpoint("mock:queue:EMEA").expectedMessageCount(1);
        getMockEndpoint("mock:queue:OTHER").expectedMessageCount(0);

        // prepare a JSon document from a String
        String json = "{ \"order\": { \"customerId\": 1234, \"item\": \"Camel in Action\" } }";

        // store the order as a file which is picked up by the route
        template.sendBodyAndHeader("file://target/order", json, Exchange.FILE_NAME, "order.json");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://target/order")
                    // set a header with the region from where the customer is from
                    .setHeader("region", method(CustomerService.class, "region"))
                    // route the message according to the region, using the dynamic to
                    .toD("mock:queue:${header.region}");
            }
        };
    }
}
