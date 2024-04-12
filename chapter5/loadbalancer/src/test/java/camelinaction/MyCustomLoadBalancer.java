package camelinaction;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.processor.loadbalancer.LoadBalancerSupport;

/**
 * A custom load balancer which will pick the first processor for gold messages,
 * and the 2nd processor for other kind of messages.
 * <p/>
 * Notice we extend the {@link LoadBalancerSupport} which provides
 * all the proper start and stop logic.
 */
public class MyCustomLoadBalancer extends LoadBalancerSupport {

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        Processor target = chooseProcessor(exchange);
        try {
            target.process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
        callback.done(true);
        return true;
    }

    protected Processor chooseProcessor(Exchange exchange) {
        String type = exchange.getIn().getHeader("type", String.class);
        if ("gold".equals(type)) {
            return getProcessors().get(0);
        } else {
            return getProcessors().get(1);
        }
    }
}
