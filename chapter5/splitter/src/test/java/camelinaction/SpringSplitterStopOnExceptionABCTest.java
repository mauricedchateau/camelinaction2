package camelinaction;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.TestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * The Spring based example of {@link camelinaction.SplitterStopOnExceptionABCTest}.
 * See this class for more details.
 */
public class SpringSplitterStopOnExceptionABCTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/splitter-stop.xml");
    }

    @Test
    public void testSplitStopOnException() throws Exception {
        MockEndpoint split = getMockEndpoint("mock:split");
        // we expect 1 messages to be split since the 2nd message should fail
        split.expectedBodiesReceived("Camel rocks");

        // and no combined aggregated message since we stop on exception
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(0);

        // now send a message with an unknown letter (F) which forces an exception to occur
        try {
            template.sendBody("direct:start", "A,F,C");
            fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            CamelExchangeException cause = TestSupport.assertIsInstanceOf(CamelExchangeException.class, e.getCause());
            IllegalArgumentException iae = TestSupport.assertIsInstanceOf(IllegalArgumentException.class, cause.getCause());
            assertEquals("Key not a known word F", iae.getMessage());
        }

        MockEndpoint.assertIsSatisfied(context);
    }

}
