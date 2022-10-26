package mtakeshi1.playground.vectorapi;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class IntVectorAPIPipelining {

    @Param({"4"})
    private int lanes;

    @Param("1048575")
    private int arraySize;

    private VectorSpecies<Integer> species;

    private int[] lhs, rhs;
    private int constant;

    private IntVector constantVector;

    @Setup
    public void setup() {
        lhs = new int[arraySize];
        rhs = new int[arraySize];
        constant = (int) System.nanoTime();
        for (int i = 0; i < lhs.length; i++) {
            lhs[i] = (int) System.nanoTime() + i;
            rhs[i] = (int) System.currentTimeMillis() + i;
        }
        species = switch (lanes) {
            case 1 -> IntVector.SPECIES_64;
            case 2 -> IntVector.SPECIES_128;
            case 4 -> IntVector.SPECIES_256;
            case 8 -> IntVector.SPECIES_512;
            default -> throw new RuntimeException("");
        };
        constantVector = IntVector.broadcast(species, constant);
    }

    @Benchmark
    public void fma(Blackhole blackhole) {
        int bound = species.loopBound(lhs.length);
        int i;
        var sum = IntVector.zero(species);
        for (i = 0; i < bound; i += species.length()) {
            var l = IntVector.fromArray(species, lhs, i);
            var r = IntVector.fromArray(species, rhs, i);
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
        var sum0 = IntVector.zero(species);
        var sum1 = IntVector.zero(species);
        var sum2 = IntVector.zero(species);
        var sum3 = IntVector.zero(species);

        for (i = 0; i < bound; ) {
            var l = IntVector.fromArray(species, lhs, i);
            var r = IntVector.fromArray(species, rhs, i);
            sum0 = l.mul(r).add(sum0);
            i += species.length();

            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum1 = l.mul(r).add(sum1);
            i += species.length();

            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum2 = l.mul(r).add(sum2);
            i += species.length();

            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
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
        var sum0 = IntVector.zero(species);
        var sum1 = IntVector.zero(species);
        var sum2 = IntVector.zero(species);
        var sum3 = IntVector.zero(species);

        var sum4 = IntVector.zero(species);
        var sum5 = IntVector.zero(species);
        var sum6 = IntVector.zero(species);
        var sum7 = IntVector.zero(species);

        for (i = 0; i < bound; ) {
            var l = IntVector.fromArray(species, lhs, i);
            var r = IntVector.fromArray(species, rhs, i);
            sum0 = l.mul(r).add(sum0);
            i += species.length();

            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum1 = l.mul(r).add(sum1);
            i += species.length();

            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum2 = l.mul(r).add(sum2);
            i += species.length();

            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum3 = l.mul(r).add(sum3);

            i += species.length();
            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum4 = l.mul(r).add(sum4);

            i += species.length();
            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum5 = l.mul(r).add(sum5);
            i += species.length();
            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
            sum6 = l.mul(r).add(sum6);

            i += species.length();
            l = IntVector.fromArray(species, lhs, i);
            r = IntVector.fromArray(species, rhs, i);
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
                .include(IntVectorAPIPipelining.class.getSimpleName())
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
