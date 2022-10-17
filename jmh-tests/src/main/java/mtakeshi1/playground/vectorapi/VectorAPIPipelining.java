package mtakeshi1.playground.vectorapi;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class VectorAPIPipelining {

    @Param({"4"})
    private int lanes;

    @Param("1048575")
    private int arraySize;

    private VectorSpecies<Double> species;

    private double[] lhs, rhs;
    private double constant;

    private DoubleVector constantVector;

    @Setup
    public void setup() {
        lhs = new double[arraySize];
        rhs = new double[arraySize];
        constant = System.nanoTime();
        for (int i = 0; i < lhs.length; i++) {
            lhs[i] = System.nanoTime() + i;
            rhs[i] = System.currentTimeMillis() + i;
        }
        species = switch (lanes) {
            case 1 -> DoubleVector.SPECIES_64;
            case 2 -> DoubleVector.SPECIES_128;
            case 4 -> DoubleVector.SPECIES_256;
            case 8 -> DoubleVector.SPECIES_512;
            default -> throw new RuntimeException("");
        };
        constantVector = DoubleVector.broadcast(species, constant);
    }

    @Benchmark
    public void fma(Blackhole blackhole) {
        int bound = species.loopBound(lhs.length);
        int i;
        var sum = DoubleVector.zero(species);
        for (i = 0; i < bound; i += species.length()) {
            var l = DoubleVector.fromArray(species, lhs, i);
            var r = DoubleVector.fromArray(species, rhs, i);
            sum = l.fma(r, sum);
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
        var sum0 = DoubleVector.zero(species);
        var sum1 = DoubleVector.zero(species);
        var sum2 = DoubleVector.zero(species);
        var sum3 = DoubleVector.zero(species);

        for (i = 0; i < bound; ) {
            var l = DoubleVector.fromArray(species, lhs, i);
            var r = DoubleVector.fromArray(species, rhs, i);
            sum0 = l.fma(r, sum0);
            i += species.length();

            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum1 = l.fma(r, sum1);
            i += species.length();

            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum2 = l.fma(r, sum0);
            i += species.length();

            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum3 = l.fma(r, sum0);
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
        var sum0 = DoubleVector.zero(species);
        var sum1 = DoubleVector.zero(species);
        var sum2 = DoubleVector.zero(species);
        var sum3 = DoubleVector.zero(species);

        var sum4 = DoubleVector.zero(species);
        var sum5 = DoubleVector.zero(species);
        var sum6 = DoubleVector.zero(species);
        var sum7 = DoubleVector.zero(species);

        for (i = 0; i < bound; ) {
            var l = DoubleVector.fromArray(species, lhs, i);
            var r = DoubleVector.fromArray(species, rhs, i);
            sum0 = l.fma(r, sum0);
            i += species.length();

            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum1 = l.fma(r, sum1);
            i += species.length();

            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum2 = l.fma(r, sum2);
            i += species.length();

            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum3 = l.fma(r, sum3);

            i += species.length();
            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum4 = l.fma(r, sum4);

            i += species.length();
            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum5 = l.fma(r, sum5);
            i += species.length();
            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum6 = l.fma(r, sum6);

            i += species.length();
            l = DoubleVector.fromArray(species, lhs, i);
            r = DoubleVector.fromArray(species, rhs, i);
            sum7 = l.fma(r, sum7);
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
                .include(VectorAPIPipelining.class.getSimpleName())
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
