package camelinaction;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel routes
 */
@ApplicationScoped
public class HelloRoute extends RouteBuilder {

    // we can inject Camel endpoints (or also just use the uris directly in the Java DSL below)

    @EndpointInject("timer:foo?period=5s")
    private Endpoint input;

    @EndpointInject("log:output")
    private Endpoint output;

    @Override
    public void configure() throws Exception {
        from(input)
            // lookup bean with name helloBean
            .bean("helloBean")
            .to(output);
    }

}
