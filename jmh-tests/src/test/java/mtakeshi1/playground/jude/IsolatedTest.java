package mtakeshi1.playground.jude;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static mtakeshi1.playground.jude.VectorHelper.mod1;

@Ignore
public class IsolatedTest {

    public void evaluateIntegral(int boost, P3 shift, int c, double acc) {
        var species = DoubleVector.SPECIES_64;
        double[] countArray = new double[boost];
        double[] iterVec = Play.evaluateIterationVector(3);
        for (int i = 0; i < boost; i++) {
            countArray[i] = boost - i;
        }
        P3 iterVector = new P3(iterVec[0], iterVec[1], iterVec[2]);
//        double[] shiftVec = {shift.x(), shift.y(), shift.z()};
        double check = OptimzedVector.partialIntegral(shift, iterVector, boost, IntegrableFunction.standardFunction, acc, c);
        double actual = OptimzedVector.integralStep(boost, IntegrableFunction.standardFunction, iterVector, DoubleVector.fromArray(species, countArray, boost - c), DoubleVector.broadcast(species, shift.x()), DoubleVector.broadcast(species, shift.y()), DoubleVector.broadcast(species, shift.z()), acc);
        Assert.assertEquals("error on step: %d. Boost is %d, shift is: %s, acc: %.10g".formatted(boost - c, boost, shift, acc), check, actual, 0.0000001);
    }

    @Test
    public void test() {
        int boost = 1 << 19;
        evaluateIntegral(boost, new P3(0.28159106371762477, 0.4222498433784144, 0.24295105743737944), boost - 7, 82.52357722);
//        error or step: 7. Boost is 524288, shift is: P3[x=0.28159106371762477, y=0.4222498433784144, z=0.24295105743737944]//
        //acc: 82,52357722

    }

    @Test
    public void testY() {
        double shift = 0.4222498433784144;
        double iter = 0.8191725161050853;
        int count = 524281;

        double plain = shift + (iter * count);
        var species = DoubleVector.SPECIES_64;

        var vShift = DoubleVector.broadcast(species, shift);
        var vIter = DoubleVector.broadcast(species, iter);
        var vCounter = DoubleVector.broadcast(species, count);
        DoubleVector lanewise = vCounter.lanewise(VectorOperators.FMA, iter, vShift);
        double v = lanewise.toArray()[0];

        Assert.assertEquals(plain, v, 0.00000000001);
        double modded = mod1(lanewise).toArray()[0];
        double expected = plain % 1.0;
        Assert.assertEquals(expected, modded, 0.00000000001);

    }

    @Test
    public void testMod() {
        double y = 429477.00816593366;
        var species = DoubleVector.SPECIES_64;

    }

}
