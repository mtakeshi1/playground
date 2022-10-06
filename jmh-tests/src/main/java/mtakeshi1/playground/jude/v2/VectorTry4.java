package mtakeshi1.playground.jude.v2;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorMask;
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

public class VectorTry4 {
    private static VectorSpecies<Double> species = DoubleVector.SPECIES_512;

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

    public static void setSpecies(VectorSpecies<Double> species) {
        VectorTry4.species = species;
        ones = DoubleVector.broadcast(getSpecies(), 1.0);
        third = DoubleVector.broadcast(getSpecies(), 1.0 / 3.0);
        zeroes = DoubleVector.broadcast(getSpecies(), 0.0);
    }

    private static DoubleVector ones = DoubleVector.broadcast(getSpecies(), 1.0);
    private static DoubleVector third = DoubleVector.broadcast(getSpecies(), 1.0 / 3.0);
    private static DoubleVector zeroes = DoubleVector.broadcast(getSpecies(), 0.0);


    public static DoubleVector f(DoubleVector x, DoubleVector y, DoubleVector z, VectorMask<Double> initialMask) {
        VectorMask<Double> notZero = x.mul(y, initialMask).mul(z, initialMask).compare(VectorOperators.NE, 0.0);
        DoubleVector oneOverY = ones.div(y, notZero);
        DoubleVector oneOverXZ = ones.div(x.add(z, notZero), notZero);
        DoubleVector vector = oneOverY.lanewise(VectorOperators.SIN, notZero).add(oneOverXZ.lanewise(VectorOperators.POW, third, notZero));
        return zeroes.blend(vector, notZero);
    }


    static Statistics evaluateStatistics(int dim, List<double[]> shifts, int boost, IntegrableFunction f) {
        double[] iterVec = evaluateIterationVector(dim);

        double avg = 0;
        double sqSum = 0;
        int k = 0;
        double[] sampleX = new double[shifts.size()];
        double[] sampleY = new double[shifts.size()];
        double[] sampleZ = new double[shifts.size()];
//
//        IntegrableFunction standardFunction = $ -> {
//            double x = $[0];
//            double y = $[1];
//            double z = $[2];
//
//            return x*y*z == 0 ? 0 : sin(1/y) + pow(1/(x+z), 1D/3);
//        };

        for (int i = 0; i < sampleX.length; i++) {
            double[] shift = shifts.get(i);
            sampleX[i] = shift[0];
            sampleY[i] = shift[1];
            sampleZ[i] = shift[2];
        }
        for (int i = 0; i < sampleX.length; i += getSpecies().length()) {
            DoubleVector shiftsX = DoubleVector.fromArray(getSpecies(), sampleX, i);
            DoubleVector shiftsY = DoubleVector.fromArray(getSpecies(), sampleY, i);
            DoubleVector shiftsZ = DoubleVector.fromArray(getSpecies(), sampleZ, i);

            double partialIntegral = 0;
            DoubleVector kVector = VectorHelper.allocateKVector(getSpecies(), 1);
            int k1;
            for (k1 = 1; k1 < boost; k1++) {
                var xp = mod1(kVector.lanewise(VectorOperators.FMA, iterVec[0], shiftsX));
                var yp = mod1(kVector.lanewise(VectorOperators.FMA, iterVec[1], shiftsY));
                var zp = mod1(kVector.lanewise(VectorOperators.FMA, iterVec[2], shiftsZ));
                var initialMask = kVector.compare(VectorOperators.LT, boost);
                var multi = f(xp, yp, zp, initialMask).toArray();
                var countVec = kVector.toArray();
                for (int j = 0; j < getSpecies().length(); j++) {
                    if (countVec[j] >= boost) {
                        break;
                    }
                    double evaluation = multi[j];
                    partialIntegral += (evaluation - partialIntegral) / k1;
                }
                kVector = kVector.add(1);
            }
            double nextAvg = avg + (partialIntegral - avg) / (++k);
            sqSum += (partialIntegral - avg) * (partialIntegral - nextAvg);
            avg = nextAvg;
            System.out.printf("[%d] - k: %s, partial: %s, avg: %s%n", i, k, partialIntegral, avg);
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
        setSpecies(DoubleVector.SPECIES_64);

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

    public static VectorSpecies<Double> getSpecies() {
        return species;
    }

}