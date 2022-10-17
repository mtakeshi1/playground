package mtakeshi1.playground.vectorapi;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode( {Mode.AverageTime})
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class VectorSumBenchmark {

    private static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_PREFERRED;

    @State(Scope.Benchmark)
    public static class VectorState {
        final double TOL = 1e-9;
        final int N = 100_000;
        final double[] u = new double[N];
        final double[] v = new double[N];

        private double uSum;
        private double vSum;

        @Setup(Level.Invocation)
        public void setup() {
            Random random = new Random(42);
            uSum = 0;
            vSum = 0;
            for (int i = 0; i < u.length; i++) {
                if (random.nextDouble() < 0.25) {
                    this.u[i] = Double.NaN;
                } else {
                    u[i] = random.nextDouble();
                    uSum += u[i];
                }
                v[i] = random.nextDouble();
                vSum += v[i];
            }
        }

        public void testUSum(double sum) {
            if (Math.abs(uSum - sum) > TOL) {
                System.out.println("\nactual:" + sum + ", expected:" + uSum);
                throw new IllegalStateException("This is wrong");
            }
        }

        private void testVSum(double sum) {
            if (Math.abs(vSum - sum) > TOL) {
                System.out.println("\nactual:" + sum + ", expected:" + vSum);
                throw new IllegalStateException("This is wrong");
            }
        }
    }

    @Benchmark
    public void testSumArrays(VectorState s) {
        double sum = 0;
        for (int i = 0; i < s.N; i++) {
            sum += s.v[i];
        }
        s.testVSum(sum);
    }

    @Benchmark
    public void testSumVectorized(VectorState s) {
        int i;
        int bound = SPECIES.loopBound(s.N);
        double sum = 0;
        for (i = 0; i < bound; i += SPECIES.length()) {
            var va = DoubleVector.fromArray(SPECIES, s.v, i);
            sum += va.reduceLanes(VectorOperators.ADD);
        }
        for (; i < s.N; i++) {
            sum += s.v[i];
        }
        s.testVSum(sum);
    }

//    @Benchmark
    public void testSumNanArrays(VectorState s) {
        double sum = 0;
        for (int i = 0; i < s.N; i++) {
            if (Double.isNaN(s.u[i])) {
                continue;
            }
            sum += s.u[i];
        }
        s.testUSum(sum);
    }

//    @Benchmark
    public void testSumNanVectorized(VectorState s) {
        int i;
        int bound = SPECIES.loopBound(s.N);
        double sum = 0;
        for (i = 0; i < bound; i += SPECIES.length()) {
            var va = DoubleVector.fromArray(SPECIES, s.u, i);
            var vm = va.test(VectorOperators.IS_NAN).not();
            sum += va.reduceLanes(VectorOperators.ADD, vm);
        }
        for (; i < s.N; i++) {
            if(Double.isNaN(s.u[i])) {
                continue;
            }
            sum += s.u[i];
        }
        s.testUSum(sum);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(VectorSumBenchmark.class.getSimpleName())
                .jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
                .forks(1)
                .mode(Mode.AverageTime)
//                .addProfiler("perfasm")
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }
}