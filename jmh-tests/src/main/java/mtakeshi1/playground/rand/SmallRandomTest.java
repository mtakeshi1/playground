package mtakeshi1.playground.rand;

import mtakeshi1.playground.mhandles.MethodHandleTests;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Measurement(timeUnit = TimeUnit.NANOSECONDS)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class SmallRandomTest {

    private static final Random rand = new Random();
    private static final ThreadLocalRandom secure = ThreadLocalRandom.current();

    @Benchmark
    public void regularRandom(Blackhole blackhole) {
        blackhole.consume(rand.nextInt());
        blackhole.consume(rand.nextDouble());
    }

    @Benchmark
    public void threadLocalRandom(Blackhole blackhole) {
        blackhole.consume(secure.nextInt());
        blackhole.consume(secure.nextDouble());

    }


    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(SmallRandomTest.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();

    }



}
