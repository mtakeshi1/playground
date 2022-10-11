package mtakeshi1.playground.jude;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

@FunctionalInterface
public interface IntegrableFunction {
    double evaluate(double... args);

    IntegrableFunction standardFunction = $ -> {
        double x = $[0];
        double y = $[1];
        double z = $[2];

        return x*y*z == 0 ? 0 : sin(1/y) + pow(1/(x+z), 1D/3);
    };



}
