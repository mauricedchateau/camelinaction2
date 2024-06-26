package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class PingServiceTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new PingService();
    }

    @Test
    public void testPing() throws Exception {
        String reply = template.requestBody("http://localhost:8080/rest/ping", null, String.class);
        assertEquals("PONG", reply);

        reply = template.requestBody("http://localhost:8080/rest/route/status", null, String.class);
        assertEquals("Started", reply);
    }

}
