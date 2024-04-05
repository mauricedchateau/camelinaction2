package camelinaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PurchaseOrderJaxbTest extends CamelSpringTestSupport {

    @Test
    public void testJaxb() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:order");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(PurchaseOrder.class);

        PurchaseOrder order = new PurchaseOrder();
        order.setName("Camel in Action");
        order.setPrice(6999);
        order.setAmount(1);

        template.sendBody("direct:order", order);

        MockEndpoint.assertIsSatisfied(context);
    }

    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/order-jaxb.xml");
    }
}
