package mtakeshi1.playground.jude;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public interface VectorHelper {
    static DoubleVector allocateKVector(VectorSpecies<Double> species, int initial) {
        double[] vec = new double[species.length()];
        for(int i = 0; i < vec.length; i++) {
            vec[i] = initial + i;
        }
        return DoubleVector.fromArray(species, vec, 0);
    }
    static DoubleVector mod1(DoubleVector vector) {
        return vector.sub(vector.convert(VectorOperators.D2L, 0).convert(VectorOperators.L2D, 0));
    }

    static boolean isClose(double v, double pointX) {
        return Math.abs(v - pointX) < 0.0000001;
    }
}
