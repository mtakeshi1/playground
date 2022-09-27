package mtakeshi1.playground.mhandles;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

//@CompilerControl(Mode.DONT_INLINE)
@Measurement(timeUnit = TimeUnit.NANOSECONDS)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class MethodHandleTests {


    @Benchmark
    public void manualIfElse(Blackhole blackhole) {
        for (int i = 0; i < 100_000; i++) {
            MHInstances.manual(blackhole, i);
        }
    }

    @Benchmark
    public void methodHandleExact(Blackhole blackhole) throws Throwable {
        for (int i = 0; i < 100_000; i++) {
            blackhole.consume(MHInstances.methodHandle.invokeExact(i));
        }
    }

    @Benchmark
    public void methodHandleInvoke(Blackhole blackhole) throws Throwable {
        for (int i = 0; i < 100_000; i++) {
            blackhole.consume(MHInstances.methodHandle.invoke(i));
        }
    }


    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(MethodHandleTests.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .addProfiler("perfasm")
                .build();

        new Runner(opt).run();

    }


}
