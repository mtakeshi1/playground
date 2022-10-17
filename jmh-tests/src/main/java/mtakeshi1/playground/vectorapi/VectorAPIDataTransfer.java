package mtakeshi1.playground.vectorapi;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(org.openjdk.jmh.annotations.Mode.AverageTime)
@State(Scope.Benchmark)
public class VectorAPIDataTransfer {

    @Param({"2", "4", "8"})
    private int lanes;

    private VectorSpecies<Double> species;

    private double[] lhs, rhs;
    private double constant;

    private DoubleVector constantVector;

    private DoubleVector something;

    @Setup
    public void setup() {
        lhs = new double[1000];
        rhs = new double[1000];
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
        something = DoubleVector.fromArray(species, lhs, 0);
    }

    @Benchmark
    public void pureLoad(Blackhole blackhole) {
        blackhole.consume(something);
    }

    @Benchmark
    public void loadAndToArray(Blackhole blackhole) {
        blackhole.consume(something.toArray());
    }

    @Benchmark
    public void toArrayAndFromArray(Blackhole blackhole) {
        double[] array = something.toArray();
        blackhole.consume(array);
        blackhole.consume(DoubleVector.fromArray(species, array, 0));
    }


    public static void main(String[] args) throws Throwable {
        Options opt = new OptionsBuilder()
                .include(VectorAPIDataTransfer.class.getSimpleName())
                .jvmArgs("--add-modules", "jdk.incubator.vector", "--enable-preview")
                .forks(1)
                .mode(Mode.AverageTime)
//                .addProfiler("perfasm")
                .timeUnit(TimeUnit.MICROSECONDS)
                .build();

        new Runner(opt).run();

    }
}
