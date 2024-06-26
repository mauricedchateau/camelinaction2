package camelinaction;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class OrderRouterWithRecipientListTest extends CamelTestSupport {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        // create CamelContext
        CamelContext camelContext = super.createCamelContext();
        
        // connect to embedded ActiveMQ JMS broker
        ConnectionFactory connectionFactory =
            new ActiveMQConnectionFactory("vm://localhost");
        camelContext.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        return camelContext;
    }
    
    @Test
    public void testPlacingOrders() throws Exception {
        getMockEndpoint("mock:accounting").expectedMessageCount(2);
        getMockEndpoint("mock:production").expectedMessageCount(1);
        MockEndpoint.assertIsSatisfied(context);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // load file orders from src/data into the JMS queue
                from("file:src/data?noop=true").to("jms:incomingOrders");
        
                // content-based router
                from("jms:incomingOrders")
	                .choice()
	                    .when(header("CamelFileName").endsWith(".xml"))
	                        .to("jms:xmlOrders")  
	                    .when(header("CamelFileName").regex("^.*(csv|csl)$"))
	                        .to("jms:csvOrders")
	                    .otherwise()
	                        .to("jms:badOrders");        
                
                from("jms:xmlOrders")                
	                .setHeader("recipients", method(RecipientsBean.class, "recipients"))
	                .recipientList(header("recipients"));
               
                from("jms:accounting")        
	                .log("Accounting received order: ${header.CamelFileName}")
	                .to("mock:accounting");                
                
                from("jms:production")        
	                .log("Production received order: ${header.CamelFileName}")
	                .to("mock:production");                
            }
        };
    }
}
