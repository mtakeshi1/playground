package mtakeshi1.playground;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;


@CompilerControl(Mode.DONT_INLINE)
@Measurement(timeUnit = TimeUnit.NANOSECONDS)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
public class MethodCallBenchmark {
    @Benchmark
    public void invokeLambda(Blackhole blackhole) {
        blackhole.consume(DoNothingInstances.lambda.doNothing(10));
    }

    @Benchmark
    public void methodReference(Blackhole blackhole) {
        blackhole.consume(DoNothingInstances.methodReference.doNothing(10));
    }

    @Benchmark
    public void anonymousClass(Blackhole blackhole) {
        blackhole.consume(DoNothingInstances.anonymousClass.doNothing(10));
    }

    @Benchmark
    public void invokeVirtual(Blackhole blackhole) {
        blackhole.consume(DoNothingInstances.regularInstanceInvokeVirtual.doNothing(10));
    }

    @Benchmark
    public void invokeInterface(Blackhole blackhole) {
        blackhole.consume(DoNothingInstances.regularInstanceInvokeInterface.doNothing(10));
    }

    @Benchmark
    public void methodHandleInvoke(Blackhole blackhole) throws Throwable {
        blackhole.consume(DoNothingInstances.doNothingMethodHandle.invoke(DoNothingInstances.regularInstanceInvokeVirtual, 10));
    }

    @Benchmark
    public void methodHandleInvokeExact(Blackhole blackhole) throws Throwable {
        DoNothing nothing = DoNothingInstances.regularInstanceInvokeVirtual;
        blackhole.consume(DoNothingInstances.doNothingMethodHandle.invokeExact(nothing, Integer.valueOf(10)));
    }

    @Benchmark
    public void reflection(Blackhole blackhole) throws Throwable {
        blackhole.consume(DoNothingInstances.doNothingReflected.invoke(DoNothingInstances.regularInstanceInvokeVirtual, 10));
    }

    @Benchmark
    public void invokeStatic(Blackhole blackhole) {
        blackhole.consume(DoNothingInstances.doNothingHere(10));
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(MethodCallBenchmark.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();

    }

}
