package mtakeshi1.playground.jude;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.lang.Math.*;
import static java.util.Collections.unmodifiableList;

public class Play {

    public static double[] evaluateIterationVector(int dim) {
        double phi = NaN; {
            record Stack(double cur, double prev) {
                boolean hasConverged() {
                    return abs(cur - prev) < 0.00000001;
                }
                Stack iterate(int dim) {
                    return new Stack(
                            pow((1+cur), 1.0 / (1 + dim)),
                            cur
                    );
                }
            }
            for(
                    var stack = new Stack(1, 0);
                    !stack.hasConverged();
                    stack = stack.iterate(dim)
            ) phi = stack.cur();
        } assert ! isNaN(phi);

        double invertOfPhi = 1/phi; // Mainly to have it effectively final...

        return IntStream.range(0, dim)
                .mapToDouble(k -> pow(invertOfPhi, k))
                .toArray();
    }

    static double evaluateIntegral(double[] shiftVec, double[] iterVec, int boost, IntegrableFunction f, int x) {
        record Stack(int count, double integral) {
            boolean isTerminated() {
                return count == 0;
            }
        }
        Stack stack = new Stack(boost, 0D);
        while(! stack.isTerminated()) {
            int count = stack.count;
            double integral = stack.integral;

            double[] point = IntStream.range(0, shiftVec.length)
                    .mapToDouble(i -> (shiftVec[i] + count * iterVec[i]) % 1.0)
                    .toArray();
            double evaluation = f.evaluate(point);
            stack = new Stack(
                    count - 1,
                    integral + (evaluation - integral) / (boost - stack.count + 1)
            );
            if (x == 6) {
                System.out.printf("%s %d %.10g %n", Arrays.toString(point), count, stack.integral);
            }
        }
//        System.out.printf("%s - integrale: %.8g%n", Arrays.toString(shiftVec), stack.integral);
        return stack.integral;
    }

    public static Statistics evaluateStatistics2(int dim, LinkedList list, int boost, IntegrableFunction f) {
        double[] iterVec = evaluateIterationVector(dim);
        return evaluateStatisticsR(list, boost, f, iterVec, 1, 0, 0, 0);
    }

    static Statistics evaluateStatisticsR(LinkedList list, int boost, IntegrableFunction f, double[] iterVec, int k, double avg, double sqSum, int x) {
        if (list == null) {
            double variance = sqSum / (k - 1);
            double stdDeviation = sqrt(variance);
            return new Statistics(
                    avg, variance,
                    new double[] {avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation}
            );
        } else {
            double integral = evaluateIntegral(list.val(), iterVec, boost, f, x);
            double nextAvg = avg + (integral - avg) / k;
            double nextSqSum = sqSum + (integral - avg) * (integral - nextAvg);
            return evaluateStatisticsR(list.next(), boost, f, iterVec, k+1, nextAvg, nextSqSum, x + 1);
        }
    }

    public static Statistics evaluateStatistics(int dim, List<double[]> shifts, int boost, IntegrableFunction f) {
        double[] iterVec = evaluateIterationVector(dim);
        record Stack(List<double[]> shifts, int k, double avg, double sqSum) {
            boolean isTerminated() { return shifts().isEmpty(); }
        }

        var stack = new Stack(shifts, 1, 0D, 0D);
        int x= 0;
        while(! stack.isTerminated()) {
            double integral = evaluateIntegral(stack.shifts.get(0), iterVec, boost, f, x++);
            double nextAvg = stack.avg() + (integral - stack.avg()) / stack.k();
            double nextSqSum = stack.sqSum() + (integral - stack.avg()) * (integral - nextAvg);
            stack = new Stack(
                    stack.shifts().subList(1, stack.shifts().size()),
                    stack.k() + 1,
                    nextAvg,
                    nextSqSum
            );
        }

        double avg = stack.avg();
        double variance = stack.sqSum() / (stack.k() - 1);
        double stdDeviation = sqrt(variance);

        return new Statistics(
                avg, variance,
                new double[] {avg - 2.1 * stdDeviation, avg + 2.1 * stdDeviation}
        );
    }

    public static void main(String[] args) {
        int dimension = 3;
        int sampleSize = 1 << 3;
        int boost = 1 << 19;

        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x*y*z == 0 ? 0 : sin(1/y) + pow(1/(x+z), 1D/3);
        };

        long ts = System.nanoTime();
        List<double[]> sample; {
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