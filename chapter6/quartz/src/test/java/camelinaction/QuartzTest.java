package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class QuartzTest extends CamelTestSupport {

    @Test
    public void testPrintFile() throws Exception {
        getMockEndpoint("mock:end").expectedMessageCount(1);

        MockEndpoint.assertIsSatisfied(context);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("quartz2://myTimer?trigger.repeatInterval=2000&trigger.repeatCount=-1")
                .setBody().simple("I was fired at ${header.fireTime}")
                .to("stream:out")
                .to("mock:end"); 
            }
        };
    }
}
