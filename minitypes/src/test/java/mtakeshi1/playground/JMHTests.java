package mtakeshi1.playground;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
//@CompilerControl(CompilerControl.Mode.DONT_INLINE)
public class JMHTests {

    @Benchmark
    public void primitive(Blackhole blackhole) {
        blackhole.consume(calculateTotalAmount(1000, 0.12, 12));
    }

    @Benchmark
    public void wrapper(Blackhole blackhole) {
        blackhole.consume(InterestRateCalculatorV2.calculateTotalAmount(new InterestRateCalculatorV2.MonetaryValue(1000), 0.12, 12));
    }

    public static double calculateTotalAmount(double initialValue, double interest, int numberOfParcels) {
        double sum = initialValue;
        for (int i = 0; i < numberOfParcels; i++) {
            sum += interest;
        }
        return sum;
    }

    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(JMHTests.class.getSimpleName())
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .addProfiler("perfasm")
                .build();

        new Runner(opt).run();

    }


}
