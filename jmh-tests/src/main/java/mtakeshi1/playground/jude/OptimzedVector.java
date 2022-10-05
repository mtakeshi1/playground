package mtakeshi1.playground.jude;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

public class OptimzedVector {

    record P3(double x, double y, double z) {
        public P3() {
            this(0, 0, 0);
        }

        public P3 times(double scalar) {
            return new P3(x * scalar, y * scalar, z * scalar);
        }

        public P3 plus(P3 other) {
            return new P3(this.x + other.x, this.y + other.y, this.z + other.z);
        }

        public P3 mod1() {
            return new P3(x % 1.0, y % 1.0, z % 1.0);
        }
    }

    static double evaluateIntegral(P3 shiftVec, P3 iterVec, int boost, IntegrableFunction f) {
        double integral = 0;
        for (int count = boost; count >= 0; count--) {
            P3 point = shiftVec.plus(iterVec.times(count)).mod1();
            double evaluation = f.evaluate(point.x(), point.y(), point.z());
            integral = integral + (evaluation - integral) / (boost - count + 1);
        }
        return integral;
    }

    public static Statistics evaluateStatistics2(int dim, List<double[]> list, int boost, IntegrableFunction f) {
        double[] iterVec = Play.evaluateIterationVector(dim);
        double[] x = new double[list.size()];
        double[] y = new double[list.size()];
        double[] z = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            x[i] = list.get(i)[0];
            y[i] = list.get(i)[1];
            z[i] = list.get(i)[2];
        }
        return evaluateStatisticsR(x, y, z, boost, f, new P3(iterVec[0], iterVec[1], iterVec[2]));
    }

    public static DoubleVector mod1(DoubleVector vector) {
        return vector.sub(vector.convert(VectorOperators.D2L, 0).convert(VectorOperators.L2D, 0));
    }

    private static double evaluateIntegralVectorOld(int boost, IntegrableFunction f, DoubleVector shiftsVector, DoubleVector iterVector) {
        double integral = 0;
        DoubleVector point;
        for (int count = boost; count >= 0; count--) {
            point = iterVector.lanewise(VectorOperators.FMA, count, shiftsVector);
            point = point.sub(point.convert(VectorOperators.D2L, 0).convert(VectorOperators.L2D, 0));
            double evaluation = f.evaluate(point.toArray());
            integral = integral + (evaluation - integral) / (boost - count + 1);
        }
        return integral;
    }


    public static Statistics evaluateStatisticsR(double[] sampleX, double[] sampleY, double[] sampleZ, int boost, IntegrableFunction f, P3 iterVec) {
        int k = 1;
        double avg = 0.0;
        double sqSum = 0.0;

//        var species = DoubleVector.SPECIES_512;

        var species = DoubleVector.SPECIES_64;

        double[] countArray = new double[boost];
        for (int i = 0; i < boost; i++) {
            countArray[i] = boost - i;
        }

        for (int i = 0; i < sampleX.length; i += species.length()) {
            DoubleVector shiftsX = DoubleVector.fromArray(species, sampleX, i);
            DoubleVector shiftsY = DoubleVector.fromArray(species, sampleY, i);
            DoubleVector shiftsZ = DoubleVector.fromArray(species, sampleZ, i);

            double partialIntegral = 0;
            for (int countIndex = 0; countIndex < boost; countIndex++) {
                DoubleVector count = DoubleVector.fromArray(species, countArray, countIndex);
                var xp = mod1(count.lanewise(VectorOperators.FMA, iterVec.x, shiftsX)).toArray();
                var yp = mod1(count.lanewise(VectorOperators.FMA, iterVec.y, shiftsY)).toArray();
                var zp = mod1(count.lanewise(VectorOperators.FMA, iterVec.z, shiftsZ)).toArray();
                var ca = count.toArray();
                for (int j = 0; j < xp.length; j++) {
                    double evaluation = f.evaluate(xp[j], yp[j], zp[j]);
                    partialIntegral = partialIntegral + (evaluation - partialIntegral) / (boost - ca[j] + 1);
                }
                if (i == 6) {
                    System.out.printf("%s %s %s %s - %.10g%n", Arrays.toString(xp), Arrays.toString(yp), Arrays.toString(zp), Arrays.toString(ca), partialIntegral);
                }
            }
//            System.out.printf("[%s %s %s] - integrale: %.8g%n", shiftsX, shiftsY, shiftsZ, partialIntegral);
            double nextAvg = avg + (partialIntegral - avg) / k;
            sqSum = sqSum + (partialIntegral - avg) * (partialIntegral - nextAvg);
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

    static double evaluateIntegral(double[] shiftVec, DoubleVector iterVector, int boost, IntegrableFunction f) {
        DoubleVector shiftsVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, Arrays.copyOf(shiftVec, 4), 0);
        return evaluateIntegralVector(boost, f, shiftsVector, iterVector);
    }

    private static double evaluateIntegralVector(int boost, IntegrableFunction f, DoubleVector shiftsVector, DoubleVector iterVector) {
        double integral = 0;
        DoubleVector point;
        for (int count = boost; count >= 0; count--) {
            point = iterVector.lanewise(VectorOperators.FMA, count, shiftsVector);
            point = point.sub(point.convert(VectorOperators.D2L, 0).convert(VectorOperators.L2D, 0));
            double evaluation = f.evaluate(point.toArray());
            integral = integral + (evaluation - integral) / (boost - count + 1);
        }
        return integral;
    }

    public static Statistics evaluateStatistics(int dim, LinkedList list, int boost, IntegrableFunction f) {
        DoubleVector iterVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, Arrays.copyOf(CalculationFacade.evaluateIterationVector(dim), 4), 0);
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

    public static void main(String[] args) {
        int dimension = 3;
        int sampleSize = 1 << 3;
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
        var statistics = evaluateStatistics2(dimension, sample, boost, f);

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
