package mtakeshi1.playground.jude.v2;

import mtakeshi1.playground.jude.IntegrableFunction;
import mtakeshi1.playground.jude.Statistics;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

public class Play3d {

    static double[] evaluateIterationVector(int dim) {
        double phi;
        double prev = 0;
        double cur = 1;
        while (abs(cur - prev) > 0.00000001) {
            prev = cur;
            cur = pow((1 + cur), 1.0 / (1 + dim));
        }
        phi = cur;
        double[] vec = new double[dim];
        for (int i = 0; i < vec.length; i++) {
            vec[i] = pow(1 / phi, i + 1);
        }
        return vec;
    }

    static double evaluateIntegral(double[] shiftVec, double[] iterVec, int boost, IntegrableFunction f) {
        double[] point = new double[shiftVec.length];

        double integral = 0;
        for (int k = 1; k < boost; k++) {
            for (int j = 0; j < point.length; j++){
                point[j] = (shiftVec[j] + k * iterVec[j]) % 1.0;
            }
            double evaluation = f.evaluate(point);
            integral += (evaluation - integral) / k;
        }
        return integral;
    }

    static Statistics evaluateStatistics(int dim, List<double[]> shifts, int boost, IntegrableFunction f) {
        double[] iterVec = evaluateIterationVector(dim);

        double avg = 0;
        double sqSum = 0;

        {
            int k = 0;
            for (var shift : shifts) {
                double integral = evaluateIntegral(shift, iterVec, boost, f);
                double nextAvg = avg + (integral - avg) / (++k);
                sqSum += (integral - avg) * (integral - nextAvg);
                avg = nextAvg;
            }
        }

        double variance = sqSum / (shifts.size() - 1);
        double stdDeviation = sqrt(variance);

        return new Statistics(
                avg, variance,
                new double[]{avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation}
        );
    }

    public static void main(String[] args) {
        long tic = System.currentTimeMillis();
        int dimension = 3;
        int sampleSize = 1 << 5;
        int boost = 1 << 19;

        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
        };

        List<double[]> sample;
        {
            var random = new Random(10);
            sample = unmodifiableList(Stream.generate(
                            () -> DoubleStream
                                    .generate(random::nextDouble)
                                    .limit(dimension)
                                    .toArray()
                    )
                    .limit(sampleSize)
                    .toList());
        }

        var statistics = evaluateStatistics(dimension, sample, boost, f);

        System.out.printf("""
                        Avg: %f
                        Variance: %f
                        Confident Interval 95%%: %s
                                        
                        Elapsed Time: %f
                        """,
                statistics.avg(), statistics.variance(),
                Arrays.toString(statistics.confidentInterval95()),
                (System.currentTimeMillis() - tic) * statistics.avg() / 1.56
        );
    }

}