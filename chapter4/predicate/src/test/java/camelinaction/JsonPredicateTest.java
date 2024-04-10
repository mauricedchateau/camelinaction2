package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.junit5.TestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test to demonstrate using @JsonPath as bean predicates during routing
 */
public class JsonPredicateTest extends CamelTestSupport {

    @BeforeEach
    public void setUp() throws Exception {
        TestSupport.deleteDirectory("target/order");
        super.setUp();
    }

    @Test
    public void sendGoldOrder() throws Exception {
        getMockEndpoint("mock:queue:gold").expectedMessageCount(1);
        getMockEndpoint("mock:queue:silver").expectedMessageCount(0);
        getMockEndpoint("mock:queue:regular").expectedMessageCount(0);

        // prepare a JSon document from a String
        String json = "{ \"order\": { \"loyaltyCode\": 88, \"item\": \"ActiveMQ in Action\" } }";

        // store the order as a file which is picked up by the route
        template.sendBodyAndHeader("file://target/order", json, Exchange.FILE_NAME, "order.json");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    public void sendSilverOrder() throws Exception {
        getMockEndpoint("mock:queue:gold").expectedMessageCount(0);
        getMockEndpoint("mock:queue:silver").expectedMessageCount(1);
        getMockEndpoint("mock:queue:regular").expectedMessageCount(0);

        // prepare a JSon document from a String
        String json = "{ \"order\": { \"loyaltyCode\": 4444, \"item\": \"Camel in Action\" } }";

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
                    .choice()
                        .when(method(CustomerService.class, "isGold")).to("mock:queue:gold")
                        .when(method(CustomerService.class, "isSilver")).to("mock:queue:silver")
                        .otherwise().to("mock:queue:regular");
            }
        };
    }
}
