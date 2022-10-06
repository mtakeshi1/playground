package mtakeshi1.playground.jude.v2; /**
This version uses incubated Vector API
*/

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

import jdk.incubator.vector.DoubleVector;
import mtakeshi1.playground.jude.IntegrableFunction;
import mtakeshi1.playground.jude.Statistics;

import static jdk.incubator.vector.DoubleVector.fromArray;
import static jdk.incubator.vector.DoubleVector.broadcast;

public class MonteCarlo {

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
            vec[i] = pow(1/phi, i+1);
        return vec;
    }

    private static final DoubleVector RG = fromArray(DoubleVector.SPECIES_256, new double[]{0,1,2,3}, 0);
    static double evaluateIntegral(double[] shiftVec, double[] iterVec, int boost, IntegrableFunction f) {
        double[] point = new double[shiftVec.length];
        double[][] transposedPoint = new double[shiftVec.length][4];
 
        double integral = 0;
        int k;
        for(k = 1; k < boost/4; k++) {
            var a = RG.add(k << 2);
            for(int j = 0; j < shiftVec.length; j++) {
                transposedPoint[j] = a.fma(iterVec[j], shiftVec[j]).toDoubleArray();
            }
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < shiftVec.length; j++)
                    point[j] = transposedPoint[j][i] % 1.0;
                double evaluation = f.evaluate(point);
                integral += (evaluation - integral) / (i | (k << 2));
            }
        }
        for(k *= 4; k < boost; k++) {
            for(int j = 0; j < shiftVec.length; j++)
                point[j] = (shiftVec[j] + k*iterVec[j]) % 1.0;
            double evaluation = f.evaluate(point);
            integral += (evaluation - integral) / k;
        }
        return integral;
    }

    public static Statistics evaluateStatistics(int dim, List<double[]> shifts, int boost, IntegrableFunction f) {
        var iterVec = evaluateIterationVector(dim);

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
                new double[] {avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation}
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

            return x*y*z == 0 ? 0 : sin(1/y) + pow(1/(x+z), 1D/3);
        };

        List<double[]> sample; {
            var random = new Random();
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
                (System.currentTimeMillis() - tic)* statistics.avg() / 1.56
        );
    }

}