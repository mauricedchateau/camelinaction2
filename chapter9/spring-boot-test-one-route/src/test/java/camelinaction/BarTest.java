package camelinaction;

import java.io.File;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.apache.camel.test.junit4.TestSupport.deleteDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The bar test want to test with only the BarRoute as the Camel route.
 * Therefore we need to specify which java routes to include in the filter, as done
 * with the properties on the @SpringBootTest annotation just below.
 * <p/>
 * The filter with ** / Bar* will match any Java routes that has a class name that starts with Bar, such
 * as BarRoute, BarBeerRoute etc.
 * Note you can also use an exclude filter.
 */
@DirtiesContext
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {MyApplication.class},
    properties = { "camel.springboot.java-routes-include-pattern=**/Bar*"})
public class BarTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    @Before
    public void cleanDir() throws Exception {
        // delete directories so we have a clean start
        deleteDirectory("target/inbox");
        deleteDirectory("target/bar");
    }

    @Test
    public void testBar() throws Exception {
        // assert the message is routed to foo

        // use NotifyBuilder to wait for the file to be routed
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        // create a new file in the inbox folder with the name hello.txt and containing Hello World as body
        template.sendBodyAndHeader("file://target/inbox", "Hello Bar", Exchange.FILE_NAME, "hello.txt");

        // notifier will wait for the file to be processed
        // and if that never happen it will time out after 10 seconds (default mock timeout)
        assertTrue(notify.matchesMockWaitTime());

        // test the file was moved to the foo folder
        File target = new File("target/bar/hello.txt");
        assertTrue("File should have been moved", target.exists());

        // test that its content is correct as well
        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello Bar", content);
    }

}
