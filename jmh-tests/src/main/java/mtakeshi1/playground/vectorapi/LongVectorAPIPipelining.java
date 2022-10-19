package mtakeshi1.playground.vectorapi;

import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.LongVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LongVectorAPIPipelining {

    @Param({"4"})
    private int lanes;

    @Param("1048575")
    private int arraySize;

    private VectorSpecies<Long> species;

    private long[] lhs, rhs;
    private int constant;

    private LongVector constantVector;

    @Setup
    public void setup() {
        lhs = new long[arraySize];
        rhs = new long[arraySize];
        constant = (int) System.nanoTime();
        for (int i = 0; i < lhs.length; i++) {
            lhs[i] = (int) System.nanoTime() + i;
            rhs[i] = (int) System.currentTimeMillis() + i;
        }
        species = switch (lanes) {
            case 1 -> LongVector.SPECIES_64;
            case 2 -> LongVector.SPECIES_128;
            case 4 -> LongVector.SPECIES_256;
            case 8 -> LongVector.SPECIES_512;
            default -> throw new RuntimeException("");
        };
        constantVector = LongVector.broadcast(species, constant);
    }

    @Benchmark
    public void fma(Blackhole blackhole) {
        int bound = species.loopBound(lhs.length);
        int i;
        var sum = LongVector.zero(species);
        for (i = 0; i < bound; i += species.length()) {
            var l = LongVector.fromArray(species, lhs, i);
            var r = LongVector.fromArray(species, rhs, i);
            sum = l.mul(r).add(sum);
        }
        var acc = sum.reduceLanes(VectorOperators.ADD);
        for (; i < lhs.length; i++) {
            acc += lhs[i] * rhs[i];
        }
        blackhole.consume(acc);
    }

    @Benchmark
    public void fmaUnroll4(Blackhole blackhole) {
        int bound = species.loopBound(lhs.length);
        int i;
        var sum0 = LongVector.zero(species);
        var sum1 = LongVector.zero(species);
        var sum2 = LongVector.zero(species);
        var sum3 = LongVector.zero(species);

        for (i = 0; i < bound; ) {
            var l = LongVector.fromArray(species, lhs, i);
            var r = LongVector.fromArray(species, rhs, i);
            sum0 = l.mul(r).add(sum0);
            i += species.length();

            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum1 = l.mul(r).add(sum1);
            i += species.length();

            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum2 = l.mul(r).add(sum2);
            i += species.length();

            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum3 = l.mul(r).add(sum3);
            i += species.length();
        }
        var acc = sum0.add(sum1).add(sum2).add(sum3).reduceLanes(VectorOperators.ADD);
        for (; i < lhs.length; i++) {
            acc += lhs[i] * rhs[i];
        }
        blackhole.consume(acc);
    }

    @Benchmark
    public void fmaUnroll8(Blackhole blackhole) {
        int bound = species.loopBound(lhs.length);
        int i;
        var sum0 = LongVector.zero(species);
        var sum1 = LongVector.zero(species);
        var sum2 = LongVector.zero(species);
        var sum3 = LongVector.zero(species);

        var sum4 = LongVector.zero(species);
        var sum5 = LongVector.zero(species);
        var sum6 = LongVector.zero(species);
        var sum7 = LongVector.zero(species);

        for (i = 0; i < bound; ) {
            var l = LongVector.fromArray(species, lhs, i);
            var r = LongVector.fromArray(species, rhs, i);
            sum0 = l.mul(r).add(sum0);
            i += species.length();

            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum1 = l.mul(r).add(sum1);
            i += species.length();

            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum2 = l.mul(r).add(sum2);
            i += species.length();

            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum3 = l.mul(r).add(sum3);

            i += species.length();
            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum4 = l.mul(r).add(sum4);

            i += species.length();
            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum5 = l.mul(r).add(sum5);
            i += species.length();
            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum6 = l.mul(r).add(sum6);

            i += species.length();
            l = LongVector.fromArray(species, lhs, i);
            r = LongVector.fromArray(species, rhs, i);
            sum7 = l.mul(r).add(sum7);
            i += species.length();
        }
        var acc = sum0.add(sum1).add(sum2).add(sum3).add(sum4).add(sum5).add(sum6).add(sum7).reduceLanes(VectorOperators.ADD);
        for (; i < lhs.length; i++) {
            acc += lhs[i] * rhs[i];
        }
        blackhole.consume(acc);
    }


    @Benchmark
    public void sequential(Blackhole blackhole) {
        var acc = 0.0;
        for (int i = 0; i < lhs.length; i++) {
//            acc += lhs[i] * rhs[i];
            acc = Math.fma(lhs[i], rhs[i], acc);
        }
        blackhole.consume(acc);
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(LongVectorAPIPipelining.class.getSimpleName())
                //"-XX:+UseVectorCmov",
                ////        "-XX:+UseCMoveUnconditionally",
                ////        "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"}
                .jvmArgs("--add-modules", "jdk.incubator.vector", "-XX:+UseVectorCmov", "-XX:+UseCMoveUnconditionally", "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0")
                .forks(1)
                .mode(Mode.AverageTime)
//                .addProfiler("perfasm")
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();

    }
}
