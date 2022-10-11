package mtakeshi1.playground.jude.v2;

import mtakeshi1.playground.jude.IntegrableFunction;

import java.security.SecureRandom;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;

public class Test {

    static double[] evaluateIterationVector(int dim) {
        double phi;
        {
            double prev = 0;
            double cur = 1;
            while (abs(cur - prev) > 0.00000001) {
                prev = cur;
                cur = pow((1 + cur), 1.0 / (1 + dim));
            }
            phi = cur;
        }
        double[] vec = new double[dim];
        for (int i = 0; i < vec.length; i++)
            vec[i] = pow(1 / phi, i + 1) % 1.0;
        return vec;
    }

    record ConfidentInterval(double min, double max) {
        double length() {
            return max - min;
        }
    }

    record Statistics(double avg, double variance, ConfidentInterval confidentInterval95) {
    }

    record PreStatistics(double avg, double sqSum, int size) {
        PreStatistics(double avg) {
            this(avg, 0, 1);
        }

        static PreStatistics sum(PreStatistics a, PreStatistics b) {
            var sumSize = a.size + b.size;
            var sumAvg = (a.size * a.avg + b.size * b.avg) / sumSize;
            var sumSqSum = fma(pow(a.avg - b.avg, 2), a.size * b.size / sumSize, a.sqSum + b.sqSum);

            return new PreStatistics(sumAvg, sumSqSum, sumSize);
        }

        Statistics toStatistics(double confidenceFactor) {
            var variance = sqSum / (size - 1);
            var error = confidenceFactor * sqrt(variance / size);

            return new Statistics(
                    avg, variance,
                    new ConfidentInterval(avg - error, avg + error)
            );
        }
    }

    static Statistics evaluateStatistics(int dim, Stream<double[]> shifts, int boost, IntegrableFunction f) {
        double[] step = evaluateIterationVector(dim);

        class SimpleComputationCell implements java.util.function.Function<double[], PreStatistics> {
            double estimateShiftedIntegral(double[] shift) {
                double integral = 0;
                double[] points = shift.clone();

                for (int l = 0; l < boost; ) {
                    addMod1MutFirst(points, step);
                    integral = fma(f.evaluate(points) - integral, 1. / (l += 1), integral);
                }

                return integral;
            }

            @Override
            public PreStatistics apply(double[] shift) {
                return new PreStatistics(estimateShiftedIntegral(shift));
            }

            static void addMod1MutFirst(double[] u, double[] v) {
                for (int i = 0; i < u.length; i++)
                    u[i] = addMod1(u[i], v[i]);
            }

            static double addMod1(double u, double v) {
                return (u + v) % 1.0;
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

        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];
            return fma(x, y, z);
            /*return x*y*z == 0. ? 0.
                : (sin(1./y) + 1./pow(x+z, 1./3.));*/
        };

        long tic = System.currentTimeMillis();
        var statistics = evaluateStatistics(dimension, sampleShifts(dimension, sampleSize), boost, f);

        System.out.printf("""
                        Avg: %f
                        Variance: %f
                        Confident Interval 95%%: [%s; %s]
                                         Length: %f 
                                        
                        Elapsed Time: %d
                        """,
                statistics.avg(), statistics.variance(),
                statistics.confidentInterval95().min(), statistics.confidentInterval95().max(),
                statistics.confidentInterval95.length(),
                (System.currentTimeMillis() - tic)
        );
    }

}