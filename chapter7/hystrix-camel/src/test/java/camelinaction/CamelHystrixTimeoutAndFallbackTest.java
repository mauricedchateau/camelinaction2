package camelinaction;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Hystrix using timeout and fallback with Java DSL
 */
public class CamelHystrixTimeoutAndFallbackTest extends CamelTestSupport {

    @Test
    public void testFast() throws Exception {
        // this calls the fast route and therefore we get a response
        Object out = template.requestBody("direct:start", "fast");
        assertEquals("Fast response", out);
    }

    @Test
    public void testSlow() throws Exception {
        // this calls the slow route and therefore causes a timeout which triggers the fallback
        Object out = template.requestBody("direct:start", "slow");
        assertEquals("Fallback response", out);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .hystrix()
                        // use 2 second timeout
                        .hystrixConfiguration().executionTimeoutInMilliseconds(2000).end()
                        .log("Hystrix processing start: ${threadName}")
                        .toD("direct:${body}")
                        .log("Hystrix processing end: ${threadName}")
                    .onFallback()
                        // use fallback if there was an exception or timeout
                        .log("Hystrix fallback start: ${threadName}")
                        .transform().constant("Fallback response")
                        .log("Hystrix fallback end: ${threadName}")
                    .end()
                    .log("After Hystrix ${body}");

                from("direct:fast")
                    // this is a fast route and takes 1 second to respond
                    .log("Fast processing start: ${threadName}")
                    .delay(1000)
                    .transform().constant("Fast response")
                    .log("Fast processing end: ${threadName}");

                from("direct:slow")
                    // this is a slow route and takes 3 second to respond
                    .log("Slow processing start: ${threadName}")
                    .delay(3000)
                    .transform().constant("Slow response")
                    .log("Slow processing end: ${threadName}");
            }
        };
    }
}
