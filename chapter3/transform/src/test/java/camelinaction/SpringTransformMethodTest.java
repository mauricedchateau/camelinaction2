package camelinaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * How to use transform() DSL with Spring
 */
public class SpringTransformMethodTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/SpringTransformMethodTest.xml");
    }

    @Test
    public void testTransform() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("<body>Hello<br/>How are you?</body>");

        template.sendBody("direct:start", "Hello\nHow are you?");

        MockEndpoint.assertIsSatisfied(context);
    }

}
