package mtakeshi1.playground.jude;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import jdk.incubator.vector.DoubleVector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class OptimzedVectorTest2 {

    public void evaluateIntegral(int boost, P3 shift) {
        var species = DoubleVector.SPECIES_64;
        double[] countArray = new double[boost];
        double[] iterVec = Play.evaluateIterationVector(3);
        for (int i = 0; i < boost; i++) {
            countArray[i] = boost - i;
        }
        P3 iterVector = new P3(iterVec[0], iterVec[1], iterVec[2]);
//        double[] shiftVec = {shift.x(), shift.y(), shift.z()};
        double acc = 0.0;
        for (int c = boost; c > 0; c--) {
            double check = OptimzedVector.partialIntegral(shift, iterVector, boost, IntegrableFunction.standardFunction, acc, c);
            double actual = OptimzedVector.integralStep(boost, IntegrableFunction.standardFunction, iterVector, DoubleVector.fromArray(species, countArray, boost - c), DoubleVector.broadcast(species, shift.x()), DoubleVector.broadcast(species, shift.y()), DoubleVector.broadcast(species, shift.z()), acc);
            Assert.assertEquals("error on step: %d. Boost is %d, shift is: %s, acc: %.10g".formatted(boost - c, boost, shift, acc), check, actual, 0.0000001);
            acc += check;
        }
    }


    @Property
    public void checkEachStep(double x, double y, double z) {
        evaluateIntegral(1 << 19, new P3(Math.abs(x), Math.abs(y), Math.abs(z)));
    }

}