package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Example how to use the groupExchange options.
 * <p/>
 * Please see the Spring XML file for comments
 */
public class SpringAggregateABCGroupTest extends CamelSpringTestSupport {

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/aggregate-abc-group.xml");
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testABCGroup() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        // one message expected
        mock.expectedMessageCount(1);
        // As the fix of CAMEL-6557, the message body is not empty anymore
        mock.message(0).body().isInstanceOf(List.class);

        template.sendBodyAndHeader("direct:start", "A", "myId", 1);
        template.sendBodyAndHeader("direct:start", "B", "myId", 1);
        template.sendBodyAndHeader("direct:start", "F", "myId", 2);
        template.sendBodyAndHeader("direct:start", "C", "myId", 1);

        MockEndpoint.assertIsSatisfied(context);

        // get the published exchange
        Exchange exchange = mock.getExchanges().get(0);

        // retrieve the List which contains the arrived exchanges
        List<Exchange> list = exchange.getMessage().getBody(List.class);
        assertEquals(3, list.size(), "Should contain the 3 arrived exchanges");

        // assert the 3 exchanges are in order and contains the correct body
        Exchange a = list.get(0);
        assertEquals("A", a.getIn().getBody());

        Exchange b = list.get(1);
        assertEquals("B", b.getIn().getBody());

        Exchange c = list.get(2);
        assertEquals("C", c.getIn().getBody());
    }

}
