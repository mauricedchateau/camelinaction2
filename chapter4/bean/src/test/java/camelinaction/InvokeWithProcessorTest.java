package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Using a Processor in the route to invoke HelloBean.
 * See the InvokeWithProcessorRoute class.
 */
public class InvokeWithProcessorTest extends CamelTestSupport {

    @Test
    public void testHelloBean() throws Exception {
        String reply = template.requestBody("direct:hello", "Camel in action", String.class);
        assertEquals("Hello Camel in action", reply);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new InvokeWithProcessorRoute();
    }

}
