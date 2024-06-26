package camelinaction;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Our first unit test with the Camel Test Kit using Spring Java Config.
 * We test the Hello World example of integration kits, which is moving a file.
 */
public class FirstTest extends CamelSpringTestSupport {

    public void setUp() throws Exception {
        // delete directories so we have a clean start
        deleteDirectory("target/inbox");
        deleteDirectory("target/outbox");
        super.setUp();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        // create a Spring Java Config context which is named AnnotationConfigApplicationContext
        AnnotationConfigApplicationContext acc = new AnnotationConfigApplicationContext();
        // then we must register the @Configuration class
        acc.register(MyApplication.class);
        return acc;
    }

    @Test
    public void testMoveFile() throws Exception {
        // use NotifyBuilder to wait for the file to be routed
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        // create a new file in the inbox folder with the name hello.txt and containing Hello World as body
        template.sendBodyAndHeader("file://target/inbox", "Hello World", Exchange.FILE_NAME, "hello.txt");

        // notifier will wait for the file to be processed
        // and if that never happen it will time out after 10 seconds (default mock timeout)
        assertTrue(notify.matchesMockWaitTime());

        // test the file was moved
        File target = new File("target/outbox/hello.txt");
        assertTrue("File should have been moved", target.exists());

        // test that its content is correct as well
        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello World", content);
    }
}
