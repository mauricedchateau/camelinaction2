package camelinaction;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * A simple bean to print hello.
 * <p/>
 * The bean is named using the CDI annotation @Named
 */
@Singleton
@Named("helloBean")
public class HelloBean {

    private int counter;

    public String hello() {
        return "Hello " + ++counter + " times";
    }

}
