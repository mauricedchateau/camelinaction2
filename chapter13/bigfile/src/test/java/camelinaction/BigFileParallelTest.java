package camelinaction;

import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

/**
 * Processing a big with concurrency using the parallelProcessing option.
 */
public class BigFileParallelTest extends CamelTestSupport {

    @Test
    public void testBigFile() throws Exception {
        // when the first exchange is done
        NotifyBuilder notify = new NotifyBuilder(context).whenDoneByIndex(0).create();

        long start = System.currentTimeMillis();

        System.out.println("Waiting to be done with 1 min timeout (use ctrl + c to stop)");
        notify.matches(60, TimeUnit.SECONDS);

        long delta = System.currentTimeMillis() - start;
        System.out.println("Took " + delta / 1000 + " seconds");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:target/inventory?noop=true")
                    .log("Starting to process big file: ${header.CamelFileName}")
                    // by enabling the parallel processing the output from the split EIP will
                    // be processed concurrently using the default thread pool settings
                    // which is a grow/shrink pool between 10-20 threads
                    // inactive threads will terminate after 60 seconds
                    .split(body().tokenize("\n")).streaming().parallelProcessing()
                        .bean(InventoryService.class, "csvToObject")
                        .to("direct:update")
                    .end()
                    .log("Done processing big file: ${header.CamelFileName}");

                from("direct:update")
                    .bean(InventoryService.class, "updateInventory");
            }
        };
    }
}
