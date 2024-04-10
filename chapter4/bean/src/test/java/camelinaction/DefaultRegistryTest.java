package camelinaction;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Using <s>{@link org.apache.camel.impl.JndiRegistry}</s> the default Camel {@link org.apache.camel.spi.Registry}
 * to register beans and let Camel lookup them to be used in routes.
 */
class DefaultRegistryTest {

    private CamelContext context;
    private ProducerTemplate template;

    @BeforeEach
    protected void setUp() throws Exception {
        // No explicit registry given.
        context = new DefaultCamelContext();

        // register our HelloBean under the name helloBean in the default registry
        context.getRegistry().bind("helloBean", new HelloBean());

        // create a producer template to use for testing
        template = context.createProducerTemplate();

        // add the route using an inlined RouteBuilder
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:hello").bean("helloBean", "hello");
            }
        });
        // star Camel
        context.start();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        // cleanup resources after test
        template.stop();
        context.stop();
    }

    @Test
    public void testHello() throws Exception {
        // test by sending in World and expect the reply to be Hello World
        Object reply = template.requestBody("direct:hello", "World");
        assertEquals("Hello World", reply);
    }

}
