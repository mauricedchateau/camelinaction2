package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Dynamic Router using @DynamicRouter.
 */
public class DynamicRouterAnnotationTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    // invoke the bean as if it was just a regular bean
                    // you must not use the .dynamicRouter here because we will
                    // annotatte the bean instead
                    .bean(DynamicRouterAnnotationBean.class, "route")
                    .to("mock:result");
            }
        };
    }

    @Test
    public void testDynamicRouter() throws Exception {
        getMockEndpoint("mock:a").expectedBodiesReceived("Camel");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");

        template.sendBody("direct:start", "Camel");

        MockEndpoint.assertIsSatisfied(context);
    }

}
