package camelinaction;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Our first unit test with the Blueprint Test Kit testing OSGi Blueprint XML routes.
 * We test the Hello World example of integration kits, which is moving a file.
 */
public class BlueprintFirstTest extends CamelBlueprintTestSupport {

    public void setUp() throws Exception {
        // delete directories so we have a clean start
        deleteDirectory("target/inbox");
        deleteDirectory("target/outbox");
        super.setUp();
    }

    @Override
    protected String getBlueprintDescriptor() {
        // refer to where in the classpath the blueprint XML file is located
        return "OSGI-INF/blueprint/firststep.xml";
    }

    @Test
    public void testMoveFile() throws Exception {
        // create a new file in the inbox folder with the name hello.txt and containing Hello World as body
        template.sendBodyAndHeader("file://target/inbox", "Hello World", Exchange.FILE_NAME, "hello.txt");

        // wait a while to let the file be moved
        Thread.sleep(2000);

        // test the file was moved
        File target = new File("target/outbox/hello.txt");
        assertTrue("File should have been moved", target.exists());

        // test that its content is correct as well
        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Hello World", content);
    }

}
