package mtakeshi1.playground.jude;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import jdk.incubator.vector.DoubleVector;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class OptimzedVectorTest {

    public void evaluateIntegral(int boost, P3 shift) {
        var species = DoubleVector.SPECIES_64;
        double[] countArray = new double[boost];
        double[] iterVec = Play.evaluateIterationVector(3);
        for (int i = 0; i < boost; i++) {
            countArray[i] = boost - i;
        }
        P3 iterVector = new P3(iterVec[0], iterVec[1], iterVec[2]);

//        double integral = OptimzedVector.evaluateIntegral(boost, IntegrableFunction.standardFunction, iterVector, species, countArray,
//                DoubleVector.broadcast(species, shift.x()), DoubleVector.broadcast(species, shift.y()), DoubleVector.broadcast(species, shift.z()));
        double[] shiftVec = {shift.x(), shift.y(), shift.z()};
        double integral = OptimzedVector.evaluateIntegral(shiftVec, iterVec, boost, IntegrableFunction.standardFunction);
//        double integral2 = Play.evaluateIntegral(shiftVec, iterVec, boost, IntegrableFunction.standardFunction, 0);
//        Assert.assertEquals(integral2, integral, 0.0000001);
//        double evaluateIntegral(int boost, IntegrableFunction f, OptimzedVector.P3 iterVec, VectorSpecies<Double> species, double[] countArray, DoubleVector shiftsX, jdk.incubator.vector.DoubleVector
//        shiftsY, jdk.incubator.vector.DoubleVector shiftsZ) {

    }

    @Property(trials = 1000)
    public void testFindMin(@InRange(min = "2", max = "524288") int boost, double x, double y, double z) {
        evaluateIntegral(boost, new P3(Math.abs(x), Math.abs(y), Math.abs(z)));
    }


}