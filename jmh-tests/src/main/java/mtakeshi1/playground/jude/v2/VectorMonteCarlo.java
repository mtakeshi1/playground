package mtakeshi1.playground.jude.v2;/*
Using new feature in preview; vector API
Compile with
    javac MonteCarlo.java --enable-preview -source 17 --add-modules jdk.incubator.vector
Run with
    java --add-modules jdk.incubator.vector MonteCarlo

========================================
Some results:
Boost = 1 << 19 , Sampling Size = 1 << 5
========================================

Vectorized/Multi-threaded version:
-------------------

Avg: 0,750002
Variance: 0,000000
Confident Interval 95%: [0.7499979085164705; 0.7500055819303477]

Elapsed Time: 468

Initial scalar version:
-----------------------

Avg: 0,750003
Variance: 0,000000
Confident Interval 95%: [0.7499966002160488, 0.750004114724422]

Elapsed Time: 3300
*/


import java.util.List;
import java.security.SecureRandom;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

import jdk.incubator.vector.DoubleVector;
import static jdk.incubator.vector.DoubleVector.fromArray;
import static jdk.incubator.vector.DoubleVector.zero;
import static jdk.incubator.vector.DoubleVector.broadcast;

public class VectorMonteCarlo {
    @FunctionalInterface
    interface IntegrableFunction {
        DoubleVector evaluate(DoubleVector... args);
    }

    static double[] evaluateIterationVector(int dim) {
        double phi; {
            double prev = 0;
            double cur = 1;
            while(abs(cur - prev) > 0.00000001) {
                prev = cur;
                cur = pow((1+cur), 1.0 / (1 + dim));
            }
            phi = cur;
        }
        double[] vec = new double[dim];
        for(int i = 0; i < vec.length; i++)
            vec[i] = pow(1/phi, i+1) % 1.0;
        return vec;
    }

    record ConfidentInterval(double min, double max) {
        double length() {
            return max - min;
        }
    }
    record Statistics(double avg, double variance, ConfidentInterval confidentInterval95) {}
    record PreStatistics(double avg, double sqSum, int size) {
        PreStatistics(double avg) { this(avg, 0, 1); }

        static PreStatistics sum(PreStatistics a, PreStatistics b) {
            var sumSize = a.size + b.size;
            var sumAvg = (a.size*a.avg + b.size*b.avg)/sumSize;
            var sumSqSum = fma(pow(a.avg - b.avg, 2), a.size*b.size/sumSize, a.sqSum + b.sqSum);

            return new PreStatistics(sumAvg, sumSqSum, sumSize);
        }

        Statistics toStatistics(double confidenceFactor) {
            var variance = sqSum / (size - 1);
            var error = confidenceFactor * sqrt(variance/size);

            return new Statistics(
                    avg, variance,
                    new ConfidentInterval(avg - error, avg + error)
            );
        }
    }
    static Statistics evaluateStatistics(int dim, Stream<double[]> shifts, int boost, IntegrableFunction f) {
        var species = DoubleVector.SPECIES_PREFERRED;
        final int LANE_COUNT = species.length();

        DoubleVector[] initVec, stepVec; {
            var iteration = evaluateIterationVector(dim);
            initVec = stream(iteration)
                    .mapToObj(x -> fromArray(species,
                            range(0, LANE_COUNT)
                                    .mapToDouble(i -> (i*x) % 1.0)
                                    .toArray()
                            , 0)
                    )
                    .toArray(DoubleVector[]::new);
            stepVec = stream(iteration)
                    .map(x -> (LANE_COUNT*x) % 1.0)
                    .mapToObj(x -> broadcast(species, x))
                    .toArray(DoubleVector[]::new);
        }

        class SimpleComputationCell implements java.util.function.Function<double[], PreStatistics> {
            double estimateShiftedIntegral(double[] shiftVec) {
                DoubleVector integralVec = zero(species);
                DoubleVector[] points = new DoubleVector[dim]; {
                    for(int i = 0; i < dim; i++)
                        points[i] = addMod1(broadcast(species, shiftVec[i]), initVec[i]);
                }

                for(int l = 0; l <= boost - LANE_COUNT;) {
                    addMod1MutFirst(points, stepVec);
                    integralVec = f
                            .evaluate(points).sub(integralVec)
                            .fma(broadcast(species, ((double)LANE_COUNT)/((l += LANE_COUNT))), integralVec);

                }

                return integralVec.div(LANE_COUNT).reduceLanes(jdk.incubator.vector.VectorOperators.ADD);
            }

            @Override
            public PreStatistics apply(double[] shiftVec) {
                return new PreStatistics(estimateShiftedIntegral(shiftVec));
            }
            static void addMod1MutFirst(DoubleVector[] u, DoubleVector[] v) {
                for(int i = 0; i < u.length; i++)
                    u[i] = addMod1(u[i], v[i]);
            }
            static DoubleVector addMod1(DoubleVector u, DoubleVector v) {
                assert u.lt(0.0).not().allTrue(); assert v.lt(0.0).not().allTrue();
                assert u.lt(1.0).allTrue(); assert v.lt(1.0).allTrue();
                u = u.add(v);
                return u.sub(1.0, u.lt(1.0).not());
            }
        }

        return shifts
                .map(new SimpleComputationCell())
                .unordered()
                .parallel()
                .reduce(PreStatistics::sum)
                .orElseThrow()
                .toStatistics(2.1 /* confident factor for 95% uncertainty */);
    }

    static Stream<double[]> sampleShifts(int dim, int sampleSize) {
        return Stream.generate(
                () -> DoubleStream
                        .generate(new SecureRandom()::nextDouble)
                        .limit(dim)
                        .toArray()
        ).limit(sampleSize);
    }

    public static void main(String[] args) {
        int dimension = 3;
        int sampleSize = 1 << 5;
        int boost = 1 << 19;

        System.out.printf("""
            * Warning: System preferred species for DoubleVector is
                %s,
            """, DoubleVector.SPECIES_PREFERRED);

        IntegrableFunction f = $ -> {
            DoubleVector x = $[0];
            DoubleVector y = $[1];
            DoubleVector z = $[2];

            //return x.fma(y,z);
            /* Another function, for fun
             */
            var notZero = x.mul(y).mul(z).compare(jdk.incubator.vector.VectorOperators.NE, 0.0);
            var ones = broadcast(DoubleVector.SPECIES_PREFERRED, 1.0);

            return  ones.div(y, notZero)
                    .lanewise(jdk.incubator.vector.VectorOperators.SIN)
                    .add(
                            ones.div(
                                    x.add(z)
                                            .lanewise(jdk.incubator.vector.VectorOperators.POW, 1.0/3)
                                    , notZero)
                    );
        };

//        long tic = System.currentTimeMillis();
        double anything = 0.0;
        for(int i = 0; i < 5000; i++) {
            var statistics = evaluateStatistics(dimension, sampleShifts(dimension, sampleSize), boost, f);
            anything += statistics.avg + statistics.variance;
        }
        System.out.println(anything);

//        System.out.printf("""
//                Avg: %f
//                Variance: %f
//                Confident Interval 95%%: [%s; %s]
//                                 Length: %f
//
//                Elapsed Time: %d
//                """,
//                statistics.avg(), statistics.variance(),
//                statistics.confidentInterval95().min(), statistics.confidentInterval95().max(),
//                statistics.confidentInterval95.length(),
//                (System.currentTimeMillis() - tic)
//        );
    }

}