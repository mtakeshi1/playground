package mtakeshi1.playground.jude.v2;

import jdk.incubator.vector.DoubleVector;
import mtakeshi1.playground.jude.IntegrableFunction;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static jdk.incubator.vector.DoubleVector.broadcast;

@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class JMHPlayV2 {

    private static final int dimension = 3;
    private static final int sampleSize = 1 << 5;
    private static final int boost = 1 << 19;

    static final List<double[]> sample;

    static final VectorMonteCarlo.IntegrableFunction vectorF;

    static {
        var random = new Random(10);
        sample = unmodifiableList(Stream.generate(
                        () -> DoubleStream
                                .generate(random::nextDouble)
                                .limit(dimension)
                                .toArray()
                )
                .limit(sampleSize)
                .toList());
        vectorF = $ -> {
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
    }

//    var statistics = evaluateStatistics(dimension, sample, boost, f);

//    @Benchmark
    public void sequential(Blackhole blackhole) {
        blackhole.consume(NonStreamingPlay.evaluateStatistics(dimension, sample, boost, IntegrableFunction.standardFunction));
    }

//    @Benchmark
    public void vector512(Blackhole blackhole) {
        VectorTry3.species = DoubleVector.SPECIES_512;
        blackhole.consume(VectorTry3.evaluateStatistics(dimension, sample, boost, IntegrableFunction.standardFunction));
    }

//    @Benchmark
    public void vector256(Blackhole blackhole) {
        VectorTry3.species = DoubleVector.SPECIES_256;
        blackhole.consume(VectorTry3.evaluateStatistics(dimension, sample, boost, IntegrableFunction.standardFunction));
    }

//    @Benchmark
    public void vector128(Blackhole blackhole) {
        VectorTry3.species = DoubleVector.SPECIES_128;
        blackhole.consume(VectorTry3.evaluateStatistics(dimension, sample, boost, IntegrableFunction.standardFunction));
    }

//    @Benchmark
    public void vectorAll256(Blackhole blackhole) {
        VectorTry4.setSpecies(DoubleVector.SPECIES_256);
        blackhole.consume(VectorTry4.evaluateStatistics(dimension, sample, boost, IntegrableFunction.standardFunction));
    }

    @Benchmark
    public void judeVector(Blackhole blackhole) {
        blackhole.consume(VectorMonteCarlo.evaluateStatistics(dimension, sample.stream(), boost, vectorF));
    }
    @Benchmark
    public void latestTest(Blackhole blackhole) {
        blackhole.consume(Test.evaluateStatistics(dimension, sample.stream(), boost, IntegrableFunction.standardFunction));
    }

//    @Benchmark
//    public void vectorJude(Blackhole blackhole) {
//        blackhole.consume(MonteCarlo.evaluateStatistics(dimension, sample, boost, IntegrableFunction.standardFunction));
//    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(JMHPlayV2.class.getSimpleName())
                .jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
                .addProfiler("perfasm")
                .forks(1)
                .timeUnit(TimeUnit.MILLISECONDS)
                .build();

        new Runner(opt).run();

    }


}
