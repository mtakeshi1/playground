package mtakeshi1.playground.rand;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@State(Scope.Benchmark)
public class Remainder {

    private int inputSize = 1000;

    @Param({"1", "2", "10"})
    private int mod;

    private double[] input;

    @Setup
    public void init() {
        input = new double[inputSize];
        for (int i = 0; i < inputSize; i++) {
            double in = (double) System.currentTimeMillis() / 10002.0;
            input[i] =  in % (double) mod;
        }
    }

    @Benchmark
    public void mod1Plain(Blackhole blackhole) {
        for (var d : input) {
            blackhole.consume(d % 1.0);
        }
    }

    @Benchmark
    public void mod1Trick(Blackhole blackhole) {
        for (var d : input) {
            int p = (int) d;
            blackhole.consume(d - p);
        }
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(Remainder.class.getSimpleName())
                .jvmArgs("--add-modules", "jdk.incubator.vector", "-XX:+UseVectorCmov", "-XX:+UseCMoveUnconditionally", "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0")
                .forks(1)
//                .addProfiler("perfasm")
                .build();

        new Runner(opt).run();

    }
}
