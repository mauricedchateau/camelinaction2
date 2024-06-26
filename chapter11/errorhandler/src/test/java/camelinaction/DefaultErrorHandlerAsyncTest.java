package camelinaction;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Showing how using default error handler to attempt redelivery
 * when it runs in async delayed mode.
 */
public class DefaultErrorHandlerAsyncTest extends CamelTestSupport {

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("orderService", new OrderService());
        return jndi;
    }

    @Test
    public void testOrderOk() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:queue.order");
        mock.expectedBodiesReceived("amount=1,name=Camel in Action,id=123,status=OK");

        template.sendBody("seda:queue.inbox","amount=1,name=Camel in Action");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    public void testOrderFail() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:queue.order");
        mock.expectedMessageCount(0);

        template.sendBody("seda:queue.inbox","amount=1,name=ActiveMQ in Action");

        // wait 5 seconds to let this test run as we expect 0 messages
        Thread.sleep(5000);

        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    public void testOrderFailThenOK() throws Exception {
        // only the 2nd book will pass
        MockEndpoint mock = getMockEndpoint("mock:queue.order");
        mock.expectedBodiesReceived("amount=1,name=Camel in Action,id=123,status=OK");

        template.sendBody("seda:queue.inbox","amount=1,name=ActiveMQ in Action");
        template.sendBody("seda:queue.inbox","amount=1,name=Camel in Action");

        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // context.setTracing(true);

                errorHandler(defaultErrorHandler()
                    // enable async redelivery mode (pay attention to thread names in console output)
                    .asyncDelayedRedelivery()
                    .maximumRedeliveries(2)
                    .redeliveryDelay(1000)
                    .retryAttemptedLogLevel(LoggingLevel.WARN));

                from("seda:queue.inbox")
                    .bean("orderService", "validate")
                    .bean("orderService", "enrich")
                    .log("Received order ${body}")
                    .to("mock:queue.order");
            }
        };
    }

}
