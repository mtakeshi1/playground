package mtakeshi1.playground.jude;

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
        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
        };
        blackhole.consume(Play.evaluateStatistics(dimension, sample, boost, f));
    }

    @Benchmark
    public void testRecursion(Blackhole blackhole) {
        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
        };
        blackhole.consume(Play.evaluateStatistics2(dimension, llist, boost, f));
    }
    @Benchmark
    public void testRecursionNonStream(Blackhole blackhole) {
        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
        };
        blackhole.consume(NonStreamingPlay.evaluateStatistics2(dimension, llist, boost, f));
    }


    @Benchmark
    public void testTailRecursion(Blackhole blackhole) {
        IntegrableFunction f = $ -> {
            double x = $[0];
            double y = $[1];
            double z = $[2];

            return x * y * z == 0 ? 0 : sin(1 / y) + pow(1 / (x + z), 1D / 3);
        };
        blackhole.consume(TailRecursivePlay.evaluateStatistics(dimension, sample, boost, f));
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(JMHPlay.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.MILLISECONDS)
                .build();

        new Runner(opt).run();

    }


}
