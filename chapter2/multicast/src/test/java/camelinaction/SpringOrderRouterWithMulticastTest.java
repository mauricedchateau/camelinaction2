package camelinaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.TestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringOrderRouterWithMulticastTest extends CamelSpringTestSupport {

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        TestSupport.deleteDirectory("activemq-data");
        super.setUp();
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/SpringOrderRouterWithMulticastTest.xml");
    }

    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:accounting").expectedMessageCount(1);
        getMockEndpoint("mock:production").expectedMessageCount(1);
        MockEndpoint.assertIsSatisfied(context);
    }
}
