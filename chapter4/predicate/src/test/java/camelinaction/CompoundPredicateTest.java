package camelinaction;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.processor.PredicateValidationException;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.apache.camel.builder.PredicateBuilder.not;
import static org.apache.camel.test.junit5.TestSupport.assertIsInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompoundPredicateTest extends CamelTestSupport {

    /**
     * This test should pass
     */
    @Test
    public void testCompoundPredicateValid() throws Exception {
        getMockEndpoint("mock:valid").expectedMessageCount(1);

        String xml = "<book><title>Camel in Action</title><user>Donald Duck</user></book>";
        template.sendBodyAndHeader("direct:start", xml, "source", "batch");

        MockEndpoint.assertIsSatisfied(context);
    }

    /**
     * We expect this test to fail with an exception. But want to let Camel print the exception on the console
     * so you can see the exception message, and Camel printing the compound predicate that failed
     */
    @Test()
    public void testCompoundPredicateInvalid() throws Exception {
        assertThrows(PredicateValidationException.class, () -> {
            try {
                String xml = "<book><title>Camel in Action</title><user>Claus</user></book>";
                template.sendBodyAndHeader("direct:start", xml, "source", "batch");
            } catch (CamelExecutionException e) {
                PredicateValidationException pve = assertIsInstanceOf(PredicateValidationException.class, e.getCause());
                throw pve;
            }
        });
    }

    public static boolean isAuthor(String xml) {
        return xml.contains("Claus") || xml.contains("Jonathan");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // build a compound predicate using the PredicateBuilder
                Predicate valid = PredicateBuilder.and(
                        // this xpath must return true
                        xpath("/book/title = 'Camel in Action'"),
                        // this simple must return true
                        simple("${header.source} == 'batch'"),
                        // this method call predicate must return false (as we use not)
                        not(method(CompoundPredicateTest.class, "isAuthor")));

                // use the predicate in the route using the validate eip
                from("direct:start")
                    .validate(valid)
                    .to("mock:valid");
            }
        };
    }
}
