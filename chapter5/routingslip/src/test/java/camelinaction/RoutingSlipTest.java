package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

/**
 * An example how to use Routing Slip EIP.
 * <p/>
 * This example uses a bean to compute the initial routing slip
 * which is used directly on the RoutingSlip EIP
 */
public class RoutingSlipTest extends CamelTestSupport {

    @Test
    public void testRoutingSlip() throws Exception {
        // setup expectations that only A and C will receive the message
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(0);
        getMockEndpoint("mock:c").expectedMessageCount(1);

        // send the incoming message
        template.sendBody("direct:start", "Hello World");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    public void testRoutingSlipCool() throws Exception {
        // setup expectations that all will receive the message
        getMockEndpoint("mock:a").expectedMessageCount(1);
        getMockEndpoint("mock:b").expectedMessageCount(1);
        getMockEndpoint("mock:c").expectedMessageCount(1);

        template.sendBody("direct:start", "We are Cool");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    // compute the routing slip at runtime using a bean
                    // use the routing slip EIP
                    .routingSlip(method(ComputeSlip.class));
            }
        };
    }
}
