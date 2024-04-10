package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.TestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test to demonstrate using @JsonPath and @Bean annotations in the {@link JSonOrderService} bean.
 */
public class JsonOrderTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/jsonOrder.xml");
    }

    @BeforeEach
    public void setUp() throws Exception {
        TestSupport.deleteDirectory("target/order");
        super.setUp();
    }

    @Test
    public void sendIncomingOrder() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:queue:order");
        mock.expectedMessageCount(1);

        // prepare a JSon document from a String
        String json = "{ \"order\": { \"customerId\": 4444, \"item\": \"Camel in Action\" } }";

        // store the order as a file which is picked up by the route
        template.sendBodyAndHeader("file://target/order", json, Exchange.FILE_NAME, "order.json");

        mock.assertIsSatisfied();
    }

}
