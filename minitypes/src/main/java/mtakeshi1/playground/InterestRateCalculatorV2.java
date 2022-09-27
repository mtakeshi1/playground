package mtakeshi1.playground;

public class InterestRateCalculatorV2 {

    public static class MonetaryValue {
        private final double value;

        public MonetaryValue(double value) {
            this.value = value;
        }

        public MonetaryValue times(double rate) {
            return new MonetaryValue(this.value * rate);
        }

        public MonetaryValue plus(MonetaryValue other) {
            return new MonetaryValue(this.value + other.value);
        }

    }

    public static MonetaryValue calculateTotalAmount(MonetaryValue initialValue, double interest, int numberOfParcels) {
        MonetaryValue sum = initialValue;
        for(int i = 0; i < numberOfParcels; i++) {
            sum = sum.plus(initialValue.times(interest));
        }
        return sum;
    }
}
