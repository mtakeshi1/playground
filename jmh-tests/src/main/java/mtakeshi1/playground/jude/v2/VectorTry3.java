package mtakeshi1.playground.jude.v2;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import mtakeshi1.playground.jude.IntegrableFunction;
import mtakeshi1.playground.jude.Statistics;
import mtakeshi1.playground.jude.VectorHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;
import static mtakeshi1.playground.jude.VectorHelper.mod1;

public class VectorTry3 {
    public static VectorSpecies<Double> species = DoubleVector.SPECIES_512;

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

    static Statistics evaluateStatistics(int dim, List<double[]> shifts, int boost, IntegrableFunction f) {
        double[] iterVec = evaluateIterationVector(dim);

        double avg = 0;
        double sqSum = 0;
        int k = 0;
        double[] sampleX = new double[shifts.size()];
        double[] sampleY = new double[shifts.size()];
        double[] sampleZ = new double[shifts.size()];

        for (int i = 0; i < sampleX.length; i++) {
            double[] shift = shifts.get(i);
            sampleX[i] = shift[0];
            sampleY[i] = shift[1];
            sampleZ[i] = shift[2];
        }
        for (int i = 0; i < sampleX.length; i += species.length()) {
            DoubleVector shiftsX = DoubleVector.fromArray(species, sampleX, i);
            DoubleVector shiftsY = DoubleVector.fromArray(species, sampleY, i);
            DoubleVector shiftsZ = DoubleVector.fromArray(species, sampleZ, i);

            double partialIntegral = 0;
            DoubleVector kVector = VectorHelper.allocateKVector(species, 1);
            int k1;
//            for (k1 = 1; k1 < (boost - (species.length() - 1)); k1++) {
            for (k1 = 1; k1 < boost; k1++) {
                var xp = mod1(kVector.lanewise(VectorOperators.FMA, iterVec[0], shiftsX)).toArray();
                var yp = mod1(kVector.lanewise(VectorOperators.FMA, iterVec[1], shiftsY)).toArray();
                var zp = mod1(kVector.lanewise(VectorOperators.FMA, iterVec[2], shiftsZ)).toArray();
                double[] countVec = kVector.toArray();
                for (int j = 0; j < species.length(); j++) {
                    if (countVec[j] >= boost) {
                        break;
                    }
                    double evaluation = f.evaluate(xp[j], yp[j], zp[j]);
                    partialIntegral += (evaluation - partialIntegral) / k1;
                    kVector = kVector.add(1);
                }
            }
            double nextAvg = avg + (partialIntegral - avg) / (++k);
            sqSum += (partialIntegral - avg) * (partialIntegral - nextAvg);
            avg = nextAvg;
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

        /*
        Avg: 1,561359
        Variance: 0,000000
        Confident Interval 95%: [1.5610645497107127, 1.561654299569972]

        Elapsed Time: 1034,901055
         */
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