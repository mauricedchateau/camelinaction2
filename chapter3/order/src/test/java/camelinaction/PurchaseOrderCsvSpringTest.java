package camelinaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PurchaseOrderCsvSpringTest extends CamelSpringTestSupport {

    @SuppressWarnings("unchecked")
	@Test
    public void testCsv() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:queue.csv");
        mock.expectedMessageCount(2);

        MockEndpoint.assertIsSatisfied(context);

        List<String> line1 = mock.getReceivedExchanges().get(0).getIn().getBody(List.class);
        assertEquals("Camel in Action", line1.get(0));
        assertEquals("6999", line1.get(1));
        assertEquals("1", line1.get(2));

        List<String> line2 = mock.getReceivedExchanges().get(1).getIn().getBody(List.class);
        assertEquals("Activemq in Action", line2.get(0));
        assertEquals("4495", line2.get(1));
        assertEquals("2", line2.get(2));
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/order-csv.xml");
    }
}
