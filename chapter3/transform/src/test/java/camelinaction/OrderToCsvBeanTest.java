package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderToCsvBeanTest extends CamelTestSupport {

    @Test
    public void testOrderToCsvBean() throws Exception {
        // this is the inhouse format we want to transform to CSV
        String inhouse = "0000005555000001144120091209  2319@1108";
        template.sendBodyAndHeader("direct:start", inhouse, "Date", "20091209");

        File file = new File("target/orders/received/report-20091209.csv");
        assertTrue(file.exists(), "File should exist");

        // compare the expected file content
        String body = context.getTypeConverter().convertTo(String.class, file);
        assertEquals("0000005555,20091209,0000011441,2319,1108", body);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    // format inhouse to csv using a bean
                    .bean(new OrderToCsvBean())
                    // and save it to a file
                    .to("file://target/orders/received?fileName=report-${header.Date}.csv");
            }
        };
    }
}
