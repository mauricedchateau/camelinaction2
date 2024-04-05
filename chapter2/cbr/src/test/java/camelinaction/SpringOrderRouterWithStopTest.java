package camelinaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderRouterWithStopTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/SpringOrderRouterWithStopTest.xml");
    }

    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:xml").expectedMessageCount(1);
        getMockEndpoint("mock:csv").expectedMessageCount(2);
        getMockEndpoint("mock:bad").expectedMessageCount(1);
        getMockEndpoint("mock:continued").expectedMessageCount(3);
        
        MockEndpoint.assertIsSatisfied(context);
    }
}
