package mtakeshi1.playground;

import org.junit.Assert;
import org.junit.Test;

public class InterestRateCalculatorTest {

    @Test
    public void testZeroParcels() {
        double amount = InterestRateCalculator.calculateTotalAmount(5000, 10000, 0);
        Assert.assertEquals(5000, amount, 0.0001);
    }

    @Test
    public void testZeroRate() {
        double amount = InterestRateCalculator.calculateTotalAmount(50, 0, 111);
        Assert.assertEquals(50, amount, 0.0001);
    }

    @Test
    public void year() {
        double amount = InterestRateCalculator.calculateTotalAmount(100, 10, 12);
        Assert.assertEquals(220, amount, 0.0001);

    }

}