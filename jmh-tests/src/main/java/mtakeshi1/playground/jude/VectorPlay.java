package mtakeshi1.playground.jude;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Double.NaN;
import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

public class VectorPlay {

    static double evaluateIntegral(double[] shiftVec, DoubleVector iterVector, int boost, IntegrableFunction f) {
        DoubleVector shiftsVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, Arrays.copyOf(shiftVec, 4), 0);
        //--add-modules jdk.incubator.vector
        return evaluateIntegralVector(boost, f, shiftsVector, iterVector);
    }

    private static double evaluateIntegralVector(int boost, IntegrableFunction f, DoubleVector shiftsVector, DoubleVector iterVector) {
        double integral = 0;
        DoubleVector point;
        for (int count = boost; count >= 0; count--) {
//            VectorOperators.FMA
            point = iterVector.lanewise(VectorOperators.FMA, count, shiftsVector);
//            point = shiftsVector.add(iterVector.mul(count));
            point = point.sub(point.convert(VectorOperators.D2L, 0).convert(VectorOperators.L2D, 0));
            double evaluation = f.evaluate(point.toArray());
            integral = integral + (evaluation - integral) / (boost - count + 1);
        }
        return integral;
    }

    public static Statistics evaluateStatistics(int dim, LinkedList list, int boost, IntegrableFunction f) {
        DoubleVector iterVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, Arrays.copyOf(evaluateIterationVector(dim), 4), 0);
        return evaluateIntegralVector(list, boost, f, iterVector);
    }

    public static Statistics evaluateIntegralVector(LinkedList list, int boost, IntegrableFunction f, DoubleVector iterVector) {

        int k = 1;
        double avg = 0.0;
        double sqSum = 0.0;
        while (list != null) {
            double integral = evaluateIntegral(list.val(), iterVector, boost, f);
            double nextAvg = avg + (integral - avg) / k;
            sqSum = sqSum + (integral - avg) * (integral - nextAvg);
            avg = nextAvg;
            list = list.next();
            k++;
        }
        double variance = sqSum / (k - 1);
        double stdDeviation = sqrt(variance);
        return new Statistics(avg, variance, new double[]{avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation});
    }

    static double[] evaluateIterationVector(int dim) {
        double phi = NaN;
        record Stack(double cur, double prev) {
            boolean hasConverged() {
                return abs(cur - prev) < 0.00000001;
            }

            Stack iterate(int dim) {
                return new Stack(
                        pow((1 + cur), 1.0 / (1 + dim)),
                        cur
                );
            }
        }
        for (var stack = new Stack(1, 0); !stack.hasConverged(); stack = stack.iterate(dim)) {
            phi = stack.cur();
        }
        double invertOfPhi = 1 / phi; // Mainly to have it effectively final...
        return IntStream.range(0, dim)
                .mapToDouble(k -> pow(invertOfPhi, k))
                .toArray();
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
        var statistics = evaluateStatistics(dimension, LinkedList.fromList(sample, 0), boost, f);

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
