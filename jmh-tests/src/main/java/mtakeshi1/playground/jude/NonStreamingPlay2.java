package mtakeshi1.playground.jude;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

public class NonStreamingPlay2 {

    public static double[] evaluateIterationVector(int dim) {
        double cur = 1.0, prev = 0.0;
        while (abs(cur - prev) > 0.00000001) {
            prev = cur;
            cur = pow((1 + cur), 1.0 / (1 + dim));
        }
        double invertOfPhi = 1 / prev;
        return IntStream.range(0, dim)
                .mapToDouble(k -> pow(invertOfPhi, k))
                .toArray();
    }

    record P3(double x, double y, double z) {}

    public static Statistics evaluateStatistics2(double[][] sample, int boost, IntegrableFunction f, double[] iterVec) {
        int k = 1;
        double avg = 1.0;
        double sqSum = 0.0;
        double[] x = new double[sample.length];
        double[] y = new double[sample.length];
        double[] z = new double[sample.length];
        for(int i = 0; i < sample.length; i++) {
            x[i] = sample[i][0];
            y[i] = sample[i][1];
            z[i] = sample[i][2];
        }
        for (var shiftVec : sample) {
            double integral1 = 0;
            for (int count = boost; count >= 0; count--) {
                double[] point = new double[shiftVec.length];
                for (int i = 0; i < point.length; i++) {
                    point[i] = (shiftVec[i] + count * iterVec[i]) % 1.0;
                }
//                double evaluation = f.evaluate(point);
//                integral1 = integral1 + (evaluation - integral1) / (boost - count + 1);
            }
            double integral = integral1;
            double nextAvg = avg + (integral - avg) / k;
            sqSum = sqSum + (integral - avg) * (integral - nextAvg);
            avg = nextAvg;
            k++;
        }
        double variance = sqSum / (k - 1);
        double stdDeviation = sqrt(variance);
        return new Statistics(
                avg, variance,
                new double[]{avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation}
        );
    }

    public static void main(String[] args) {
        int dimension = 3;
        int sampleSize = 1 << 5;
        int boost = 1 << 19;

        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
        };

        long ts = System.nanoTime();
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

        var sample2 = sample.toArray(new double[sampleSize][]);

//        var statistics = evaluateStatistics(dimension, sample, boost, f);
        var statistics = evaluateStatistics2(sample2, boost, f, evaluateIterationVector(dimension));

        System.out.printf("""
                        Avg: %f
                        Variance: %f
                        Confident Interval 95%%: %s
                        """,
                statistics.avg(), statistics.variance(), Arrays.toString(statistics.confidentInterval95()));
        System.out.printf("%dms%n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - ts));
        /*

Avg: 1.579613
Variance: 0.028466
Confident Interval 95%: [1.2253066757579743, 1.9339197082285637]
1967ms

         */
    }

}