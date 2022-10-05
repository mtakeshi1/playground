package mtakeshi1.playground.jude;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public interface CalculationFacade {

    int dim = 3;

    static double[] evaluateIterationVector(int dim) {
        double cur = 1, prev = 0;
        while (abs(cur - prev) >= 0.00000001) {
            prev = cur;
            cur = pow((1 + cur), 1.0 / (1 + dim));

        }
        double invertOfPhi = 1 / prev;
        return IntStream.range(0, dim)
                .mapToDouble(k -> pow(invertOfPhi, k))
                .toArray();
    }

    double[] standardIterationVector = evaluateIterationVector(dim);

    Statistics evaluateStatistics(List<double[]> list, int boost, IntegrableFunction f);

    Statistics evaluateStatistics(LinkedList list, int boost, IntegrableFunction f);

}
