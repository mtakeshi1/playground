package mtakeshi1.playground.jude.fp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class FPWeirdness {

    static double[] small, large, tiny;

    static {
        small = new double[10];
        large = new double[10];
        tiny = new double[10];
        for (int i = 0; i < small.length; i++) {
            large[i] = 100000;
            small[i] = 1.5;
            tiny[i] = 0.5;
        }
    }

    @Benchmark
    public void lhsTiny(Blackhole blackhole) {
        blackhole.consume(tiny[5] % 1.0);
    }

    @Benchmark
    public void lhsSmall(Blackhole blackhole) {
        blackhole.consume(small[5] % 1.0);
    }

    @Benchmark
    public void lhsLarge(Blackhole blackhole) {
        blackhole.consume(large[5] % 1.0);
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(FPWeirdness.class.getSimpleName())
//                .jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
                .forks(1)
                .addProfiler("perfasm")
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();

    }
}
