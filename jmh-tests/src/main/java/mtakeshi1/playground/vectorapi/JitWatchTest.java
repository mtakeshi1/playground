package mtakeshi1.playground.vectorapi;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;

import java.util.Random;

public class JitWatchTest {

    private DoubleVector innerLoop(DoubleVector l, DoubleVector r, DoubleVector acc) {
//        return l.mul(r).add(acc);
        return l.fma(r, acc);
    }

    public double fmaVector(double[] left, double[] right) {
        var species = DoubleVector.SPECIES_PREFERRED;
        int i = 0, bound = species.loopBound(left.length);
        var acc = DoubleVector.zero(species);
        for (; i < bound; i += species.length()) {
            DoubleVector l = DoubleVector.fromArray(species, left, i);
            DoubleVector r = DoubleVector.fromArray(species, right, i);
            acc = innerLoop(l, r, acc);
        }
        double sum = 0.0;
        for (; i < left.length; i++) {
            sum = Math.fma(left[i], right[i], sum);
        }
        return acc.reduceLanes(VectorOperators.ADD) + sum;
    }

    public double fmaArray(double[] left, double[] right) {
        double sum = 0.0;
        for (int i = 0; i < left.length; i++) {
            sum = Math.fma(left[i], right[i], sum);
        }
        return sum;
    }

    public static void main(String[] args) {
        for (int j = 0; j < 10; j++) {
            Random r = new Random(10);
            int len = 1024 * 1024 * 10;
            double[] left = new double[len];
            double[] right = new double[len];
            for (int i = 0; i < left.length; i++) {
                left[i] = r.nextGaussian();
                right[i] = r.nextGaussian();
            }
            JitWatchTest test = new JitWatchTest();
            System.out.println(test.fmaVector(left, right));
        }
    }

}
