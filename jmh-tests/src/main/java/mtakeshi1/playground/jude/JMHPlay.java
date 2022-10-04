package mtakeshi1.playground.jude;

import jdk.incubator.vector.DoubleVector;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.util.Collections.unmodifiableList;

@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class JMHPlay {

    private static final int dimension = 3;
    private static final int sampleSize = 1 << 5;

    private static final int boost = 1 << 19;

    private static final List<double[]> sample;
    private static final LinkedList llist;
    private static final IntegrableFunction f = $ -> {
        double x = $[0];
        double y = $[1];
        double z = $[2];

        return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
    };

    private static final double[] iterVec = Play.evaluateIterationVector(dimension);

    private static final DoubleVector iterVector = DoubleVector.fromArray(DoubleVector.SPECIES_256, Arrays.copyOf(iterVec, 4), 0);

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
        llist = LinkedList.fromList(sample);
    }

    @Benchmark
    public void testLoop(Blackhole blackhole) {
        blackhole.consume(Play.evaluateStatistics(dimension, sample, boost, f));
    }

    @Benchmark
    public void testRecursion(Blackhole blackhole) {
        blackhole.consume(Play.evaluateStatistics2(dimension, llist, boost, f));
    }

    @Benchmark
    public void testRecursionNonStream(Blackhole blackhole) {
        blackhole.consume(NonStreamingPlay.evaluateStatistics2(dimension, llist, boost, f));
    }


    @Benchmark
    public void testTailRecursion(Blackhole blackhole) {
        blackhole.consume(TailRecursivePlay.evaluateStatistics(dimension, sample, boost, f));
    }

    @Benchmark
    public void testVector(Blackhole blackhole) {
        blackhole.consume(VectorPlay.evaluateIntegralVector(llist, boost, f, iterVector));
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(JMHPlay.class.getSimpleName())
                .jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
                .forks(1)
                .timeUnit(TimeUnit.MILLISECONDS)
                .build();

        new Runner(opt).run();

    }


}
