package mtakeshi1.playground.jude;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

public class NonStreamingPlay {

    static double evaluateIntegral(double[] shiftVec, double[] iterVec, int boost, IntegrableFunction f) {
        record Stack(int count, double integral) {
            boolean isTerminated() {
                return count == 0;
            }
        }
        Stack stack = new Stack(boost, 0D);
        while (!stack.isTerminated()) {
            int count = stack.count;
            double integral = stack.integral;


            double[] point = new double[shiftVec.length];
            for (int i = 0; i < point.length; i++) {
                point[i] = (shiftVec[i] + count * iterVec[i]) % 1.0;
            }
            double evaluation = f.evaluate(point);
            stack = new Stack(
                    count - 1,
                    integral + (evaluation - integral) / (boost - stack.count + 1)
            );
        }
        return stack.integral;
    }

    public static Statistics evaluateStatistics2(int dim, LinkedList list, int boost, IntegrableFunction f) {
        double[] iterVec = Play.evaluateIterationVector(dim);
        return evaluateStatisticsR(list, boost, f, iterVec, 1, 0, 0);
    }

    static Statistics evaluateStatisticsR(LinkedList list, int boost, IntegrableFunction f, double[] iterVec, int k, double avg, double sqSum) {
        if (list == null) {
            double variance = sqSum / (k - 1);
            double stdDeviation = sqrt(variance);
            return new Statistics(
                    avg, variance,
                    new double[]{avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation}
            );
        } else {
            double integral = evaluateIntegral(list.val(), iterVec, boost, f);
            double nextAvg = avg + (integral - avg) / k;
            double nextSqSum = sqSum + (integral - avg) * (integral - nextAvg);
            return evaluateStatisticsR(list.next(), boost, f, iterVec, k + 1, nextAvg, nextSqSum);
        }
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


//        var statistics = evaluateStatistics(dimension, sample, boost, f);
        var statistics = evaluateStatistics2(dimension, LinkedList.fromList(sample, 0), boost, f);

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